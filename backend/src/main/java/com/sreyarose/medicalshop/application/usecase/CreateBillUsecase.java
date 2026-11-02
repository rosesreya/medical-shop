
package com.sreyarose.medicalshop.application.usecase;

import com.sreyarose.medicalshop.domain.entity.Batch;
import com.sreyarose.medicalshop.domain.entity.Bill;
import com.sreyarose.medicalshop.domain.entity.BillItem;
import com.sreyarose.medicalshop.domain.entity.PaymentMethod;
import com.sreyarose.medicalshop.domain.entity.PaymentStatus;
import com.sreyarose.medicalshop.domain.entity.Product;
import com.sreyarose.medicalshop.domain.repository.BatchRepository;
import com.sreyarose.medicalshop.domain.repository.BillItemRepository;
import com.sreyarose.medicalshop.domain.repository.BillRepository;
import com.sreyarose.medicalshop.domain.repository.ProductRepository;
import com.sreyarose.medicalshop.presentation.dto.BillItemRequest;
import com.sreyarose.medicalshop.presentation.dto.BillRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CreateBillUsecase {

    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;
    private final BatchRepository batchRepository;
    private final ProductRepository productRepository;

    public CreateBillUsecase(
            BillRepository billRepository,
            BillItemRepository billItemRepository,
            BatchRepository batchRepository,
            ProductRepository productRepository
    ) {
        this.billRepository = billRepository;
        this.billItemRepository = billItemRepository;
        this.batchRepository = batchRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Bill execute(BillRequest request) {

        if (request == null
                || request.getItems() == null
                || request.getItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "A bill must contain at least one item"
            );
        }

        if (request.getPaymentMethod() == null) {
            throw new IllegalArgumentException(
                    "Payment method is required"
            );
        }

        LocalDate today = LocalDate.now();

        List<BillItem> billItems = new ArrayList<>();
        List<AllocatedItem> allocations = new ArrayList<>();

        BigDecimal subtotal = BigDecimal.ZERO;

        for (BillItemRequest itemRequest : request.getItems()) {

            if (itemRequest == null
                    || itemRequest.getProductId() == null
                    || itemRequest.getProductId() <= 0) {
                throw new IllegalArgumentException(
                        "A valid product is required"
                );
            }

            if (itemRequest.getQuantity() <= 0) {
                throw new IllegalArgumentException(
                        "Quantity must be greater than zero"
                );
            }

            BigDecimal rate = itemRequest.getSellingRate();

            if (rate == null
                    || rate.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                        "Selling rate cannot be negative or null"
                );
            }

            rate = rate.setScale(2, RoundingMode.HALF_UP);

            BigDecimal requestedDiscount =
                    itemRequest.getDiscountAmount() == null
                            ? BigDecimal.ZERO
                            : itemRequest.getDiscountAmount();

            if (requestedDiscount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                        "Discount cannot be negative"
                );
            }

            requestedDiscount = requestedDiscount.setScale(
                    2, RoundingMode.HALF_UP
            );

            Product product = productRepository
                    .findById(itemRequest.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found: "
                                    + itemRequest.getProductId()
                    ));

            if (!product.isActive()) {
                throw new IllegalArgumentException(
                        "Inactive products cannot be sold: "
                                + product.getName()
                );
            }

            BigDecimal grossAmount = rate.multiply(
                    BigDecimal.valueOf(itemRequest.getQuantity())
            ).setScale(2, RoundingMode.HALF_UP);

            if (requestedDiscount.compareTo(grossAmount) > 0) {
                throw new IllegalArgumentException(
                        "Discount cannot exceed the item amount"
                );
            }

            List<Batch> eligibleBatches =
                    batchRepository.findEligibleByProductIdForUpdate(
                            itemRequest.getProductId(),
                            today
                    );

            int remaining = itemRequest.getQuantity();

            List<Batch> selectedBatches = new ArrayList<>();
            List<Integer> selectedQuantities = new ArrayList<>();

            // Select stock using FEFO order.
            for (Batch batch : eligibleBatches) {

                if (remaining == 0) {
                    break;
                }

                int available = batch.getQuantity();

                if (available <= 0) {
                    continue;
                }

                int allocated = Math.min(available, remaining);

                selectedBatches.add(batch);
                selectedQuantities.add(allocated);

                remaining -= allocated;
            }

            if (remaining > 0) {
                throw new IllegalArgumentException(
                        "Insufficient unexpired stock for product: "
                                + product.getName()
                );
            }

            // Selling price must not exceed any selected batch's MRP.
            for (Batch batch : selectedBatches) {
                if (rate.compareTo(batch.getMrp()) > 0) {
                    throw new IllegalArgumentException(
                            "Selling rate cannot exceed MRP for batch: "
                                    + batch.getBatchNumber()
                    );
                }
            }

            BigDecimal discountAllocated = BigDecimal.ZERO;

            // Split the requested discount proportionally across
            // the selected batches, correcting rounding on the last.
            for (int i = 0; i < selectedBatches.size(); i++) {

                Batch batch = selectedBatches.get(i);
                int quantity = selectedQuantities.get(i);

                BigDecimal allocationGross = rate.multiply(
                        BigDecimal.valueOf(quantity)
                ).setScale(2, RoundingMode.HALF_UP);

                BigDecimal allocationDiscount;

                if (i == selectedBatches.size() - 1) {
                    allocationDiscount = requestedDiscount
                            .subtract(discountAllocated);
                } else {
                    allocationDiscount = requestedDiscount
                            .multiply(allocationGross)
                            .divide(
                                    grossAmount,
                                    2,
                                    RoundingMode.HALF_UP
                            );

                    discountAllocated = discountAllocated.add(
                            allocationDiscount
                    );
                }

                allocations.add(new AllocatedItem(
                        product.getId(),
                        batch,
                        quantity,
                        rate,
                        allocationDiscount,
                        product.getCgstRate(),
                        product.getSgstRate()
                ));

                // GST is included in the selling rate.
                subtotal = subtotal.add(
                        allocationGross.subtract(allocationDiscount)
                );
            }
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

        /*
         * Calculate GST totals before saving the bill header.
         * BillItem applies the same inclusive-GST calculation used
         * for the final persisted bill items.
         */
        BigDecimal totalTaxableAmount = BigDecimal.ZERO;
        BigDecimal totalCgstAmount = BigDecimal.ZERO;
        BigDecimal totalSgstAmount = BigDecimal.ZERO;

        for (AllocatedItem allocation : allocations) {

            BillItem calculatedItem = new BillItem(
                    null,
                    null,
                    allocation.productId(),
                    allocation.batch().getId(),
                    allocation.quantity(),
                    allocation.sellingRate(),
                    allocation.discountAmount(),
                    allocation.cgstRate(),
                    allocation.sgstRate()
            );

            totalTaxableAmount = totalTaxableAmount.add(
                    calculatedItem.getTaxableAmount()
            );

            totalCgstAmount = totalCgstAmount.add(
                    calculatedItem.getCgstAmount()
            );

            totalSgstAmount = totalSgstAmount.add(
                    calculatedItem.getSgstAmount()
            );
        }

        totalTaxableAmount = totalTaxableAmount.setScale(
                2, RoundingMode.HALF_UP
        );
        totalCgstAmount = totalCgstAmount.setScale(
                2, RoundingMode.HALF_UP
        );
        totalSgstAmount = totalSgstAmount.setScale(
                2, RoundingMode.HALF_UP
        );

        PaymentMethod method = request.getPaymentMethod();

        PaymentStatus paymentStatus;
        BigDecimal amountReceived;
        BigDecimal changeAmount;

        if (method == PaymentMethod.CASH) {

            amountReceived = request.getAmountReceived();

            if (amountReceived == null
                    || amountReceived.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                        "Valid cash received amount is required"
                );
            }

            amountReceived = amountReceived.setScale(
                    2, RoundingMode.HALF_UP
            );

            if (amountReceived.compareTo(subtotal) < 0) {
                throw new IllegalArgumentException(
                        "Cash received is less than the bill total"
                );
            }

            paymentStatus = PaymentStatus.PAID;
            changeAmount = amountReceived.subtract(subtotal)
                    .setScale(2, RoundingMode.HALF_UP);

        } else {
            paymentStatus = PaymentStatus.PENDING;
            amountReceived = BigDecimal.ZERO.setScale(2);
            changeAmount = BigDecimal.ZERO.setScale(2);
        }

        BigDecimal billDiscount = BigDecimal.ZERO.setScale(2);

        String billNumber = "BILL-"
                + LocalDateTime.now().toString()
                    .replace("-", "")
                    .replace(":", "")
                    .replace(".", "")
                + "-"
                + UUID.randomUUID().toString()
                    .substring(0, 8)
                    .toUpperCase();

        Bill bill = new Bill(
                null,
                billNumber,
                LocalDateTime.now(),
                request.getCustomerName(),
                request.getCustomerMobile(),
                subtotal,
                billDiscount,
                subtotal,
                totalTaxableAmount,
                totalCgstAmount,
                totalSgstAmount,
                method,
                paymentStatus,
                amountReceived,
                changeAmount
        );

        Bill savedBill = billRepository.save(bill);

        // Save each allocated item and deduct stock from its
        // exact FEFO-selected batch.
        for (AllocatedItem allocation : allocations) {

            Batch batch = allocation.batch();

            batch.deductStock(allocation.quantity());
            batchRepository.save(batch);

            billItems.add(new BillItem(
                    null,
                    savedBill.getId(),
                    allocation.productId(),
                    batch.getId(),
                    allocation.quantity(),
                    allocation.sellingRate(),
                    allocation.discountAmount(),
                    allocation.cgstRate(),
                    allocation.sgstRate()
            ));
        }

        billItemRepository.saveAll(billItems);

        return savedBill;
    }

    private record AllocatedItem(
            Long productId,
            Batch batch,
            int quantity,
            BigDecimal sellingRate,
            BigDecimal discountAmount,
            BigDecimal cgstRate,
            BigDecimal sgstRate
    ) {
    }
}
