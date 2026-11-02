package com.sreyarose.medicalshop.application.usecase;

import com.sreyarose.medicalshop.domain.entity.Batch;
import com.sreyarose.medicalshop.domain.entity.Product;
import com.sreyarose.medicalshop.domain.entity.Purchase;
import com.sreyarose.medicalshop.domain.entity.PurchaseItem;
import com.sreyarose.medicalshop.domain.entity.Supplier;

import com.sreyarose.medicalshop.domain.repository.BatchRepository;
import com.sreyarose.medicalshop.domain.repository.ProductRepository;
import com.sreyarose.medicalshop.domain.repository.PurchaseItemRepository;
import com.sreyarose.medicalshop.domain.repository.PurchaseRepository;
import com.sreyarose.medicalshop.domain.repository.SupplierRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CreatePurchaseUsecase {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final BatchRepository batchRepository;

    public CreatePurchaseUsecase(
            PurchaseRepository purchaseRepository,
            PurchaseItemRepository purchaseItemRepository,
            SupplierRepository supplierRepository,
            ProductRepository productRepository,
            BatchRepository batchRepository) {

        this.purchaseRepository = purchaseRepository;
        this.purchaseItemRepository = purchaseItemRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.batchRepository = batchRepository;
    }

    @Transactional
    public Purchase execute(
            Long supplierId,
            String invoiceNumber,
            LocalDate purchaseDate,
            List<PurchaseItem> requestedItems) {

        if (supplierId == null || supplierId <= 0) {
            throw new IllegalArgumentException("Invalid supplier ID");
        }

        if (invoiceNumber == null || invoiceNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Invoice number is required");
        }

        if (purchaseDate == null) {
            throw new IllegalArgumentException(
                    "Purchase date is required");
        }

        if (purchaseDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Purchase date cannot be in the future");
        }

        if (requestedItems == null || requestedItems.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one purchase item is required");
        }

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Supplier does not exist"));

        if (!supplier.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot create a purchase for an inactive supplier");
        }

        String normalizedInvoiceNumber = invoiceNumber.trim();

        if (purchaseRepository.existsBySupplierIdAndInvoiceNumber(
                supplierId, normalizedInvoiceNumber)) {
            throw new IllegalArgumentException(
                    "This supplier invoice has already been recorded");
        }

        Set<String> batchKeys = new HashSet<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (PurchaseItem item : requestedItems) {
            if (item == null) {
                throw new IllegalArgumentException(
                        "Purchase item cannot be null");
            }

            String batchKey = item.getProductId()
                    + "::"
                    + item.getBatchNumber().trim().toUpperCase(
                            java.util.Locale.ROOT);

            if (!batchKeys.add(batchKey)) {
                throw new IllegalArgumentException(
                        "The same product and batch cannot appear twice "
                                + "in one purchase");
            }

            Product product = productRepository
                    .findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product does not exist: "
                                    + item.getProductId()));

            if (!product.isActive()) {
                throw new IllegalArgumentException(
                        "Cannot purchase an inactive product: "
                                + product.getName());
            }

            if (item.getManufacturingDate().isAfter(purchaseDate)) {
                throw new IllegalArgumentException(
                        "Manufacturing date cannot be after purchase date");
            }

            if (!item.getExpiryDate().isAfter(purchaseDate)) {
                throw new IllegalArgumentException(
                        "Cannot receive an already-expired batch");
            }

            totalAmount = totalAmount.add(item.getLineTotal());
        }

        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);

        Purchase purchase = new Purchase(
                null,
                supplierId,
                normalizedInvoiceNumber,
                purchaseDate,
                totalAmount
        );

        Purchase savedPurchase = purchaseRepository.save(purchase);

        for (PurchaseItem requestedItem : requestedItems) {

            String batchNumber = requestedItem.getBatchNumber().trim();

            Batch batch = batchRepository
                    .findByProductIdAndBatchNumberForUpdate(
                            requestedItem.getProductId(),
                            batchNumber)
                    .orElse(null);

            if (batch == null) {
                batch = new Batch(
                        null,
                        requestedItem.getProductId(),
                        batchNumber,
                        requestedItem.getManufacturingDate(),
                        requestedItem.getExpiryDate(),
                        0,
                        requestedItem.getMrp()
                );
            } else {
                boolean datesMatch =
                        batch.getManufacturingDate().equals(
                                requestedItem.getManufacturingDate())
                        && batch.getExpiryDate().equals(
                                requestedItem.getExpiryDate());

                boolean mrpMatches =
                        batch.getMrp().compareTo(
                                requestedItem.getMrp()) == 0;

                if (!datesMatch || !mrpMatches) {
                    throw new IllegalArgumentException(
                            "Batch details conflict with existing inventory: "
                                    + batchNumber);
                }
            }

            batch.addStock(requestedItem.getQuantity());
            batchRepository.save(batch);

            PurchaseItem savedItem = new PurchaseItem(
                    null,
                    savedPurchase.getId(),
                    requestedItem.getProductId(),
                    batchNumber,
                    requestedItem.getManufacturingDate(),
                    requestedItem.getExpiryDate(),
                    requestedItem.getQuantity(),
                    requestedItem.getPurchaseCost(),
                    requestedItem.getMrp()
            );

            purchaseItemRepository.save(savedItem);
        }

        return savedPurchase;
    }
}