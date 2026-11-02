
package com.sreyarose.medicalshop.domain.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BillItem {

    private final Long id;
    private final Long billId;
    private final Long productId;
    private final Long batchId;
    private final int quantity;

    private final BigDecimal sellingRate;
    private final BigDecimal discountAmount;

    private final BigDecimal taxableAmount;
    private final BigDecimal cgstRate;
    private final BigDecimal cgstAmount;
    private final BigDecimal sgstRate;
    private final BigDecimal sgstAmount;

    private final BigDecimal lineTotal;

    /*
     * Backward-compatible constructor.
     * Existing callers continue to work with zero GST.
     */
    public BillItem(
            Long id,
            Long billId,
            Long productId,
            Long batchId,
            int quantity,
            BigDecimal sellingRate,
            BigDecimal discountAmount
    ) {
        this(
                id,
                billId,
                productId,
                batchId,
                quantity,
                sellingRate,
                discountAmount,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    /*
     * Constructor for creating a new bill item.
     * Selling rate includes GST.
     */
    public BillItem(
            Long id,
            Long billId,
            Long productId,
            Long batchId,
            int quantity,
            BigDecimal sellingRate,
            BigDecimal discountAmount,
            BigDecimal cgstRate,
            BigDecimal sgstRate
    ) {
        validateIds(productId, batchId);
        validateQuantity(quantity);
        validateSellingRate(sellingRate);
        validateDiscount(discountAmount);
        validateTaxRate(cgstRate, "CGST");
        validateTaxRate(sgstRate, "SGST");

        BigDecimal grossAmount = sellingRate
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal discount = discountAmount
                .setScale(2, RoundingMode.HALF_UP);

        if (discount.compareTo(grossAmount) > 0) {
            throw new IllegalArgumentException(
                    "Discount cannot exceed the gross amount"
            );
        }

        this.id = id;
        this.billId = billId;
        this.productId = productId;
        this.batchId = batchId;
        this.quantity = quantity;
        this.sellingRate = sellingRate;
        this.discountAmount = discount;

        this.cgstRate = cgstRate;
        this.sgstRate = sgstRate;

        // Final amount payable for this line, including GST.
        this.lineTotal = grossAmount.subtract(discount)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalTaxRate = cgstRate.add(sgstRate);

        BigDecimal taxMultiplier = BigDecimal.ONE.add(
                totalTaxRate.divide(
                        BigDecimal.valueOf(100),
                        10,
                        RoundingMode.HALF_UP
                )
        );

        // Extract the taxable value from the GST-inclusive total.
        this.taxableAmount = lineTotal.divide(
                taxMultiplier,
                2,
                RoundingMode.HALF_UP
        );

        this.cgstAmount = taxableAmount
                .multiply(cgstRate)
                .divide(
                        BigDecimal.valueOf(100),
                        2,
                        RoundingMode.HALF_UP
                );

        this.sgstAmount = taxableAmount
                .multiply(sgstRate)
                .divide(
                        BigDecimal.valueOf(100),
                        2,
                        RoundingMode.HALF_UP
                );
    }

    /*
     * Private constructor for restoring persisted values.
     * It deliberately does not recalculate GST.
     */
    private BillItem(
            Long id,
            Long billId,
            Long productId,
            Long batchId,
            int quantity,
            BigDecimal sellingRate,
            BigDecimal discountAmount,
            BigDecimal taxableAmount,
            BigDecimal cgstRate,
            BigDecimal cgstAmount,
            BigDecimal sgstRate,
            BigDecimal sgstAmount,
            BigDecimal lineTotal
    ) {
        this.id = id;
        this.billId = billId;
        this.productId = productId;
        this.batchId = batchId;
        this.quantity = quantity;
        this.sellingRate = sellingRate;
        this.discountAmount = discountAmount;
        this.taxableAmount = taxableAmount;
        this.cgstRate = cgstRate;
        this.cgstAmount = cgstAmount;
        this.sgstRate = sgstRate;
        this.sgstAmount = sgstAmount;
        this.lineTotal = lineTotal;
    }

    /*
     * Factory for reconstructing an item from persisted data.
     */
    public static BillItem restore(
            Long id,
            Long billId,
            Long productId,
            Long batchId,
            int quantity,
            BigDecimal sellingRate,
            BigDecimal discountAmount,
            BigDecimal taxableAmount,
            BigDecimal cgstRate,
            BigDecimal cgstAmount,
            BigDecimal sgstRate,
            BigDecimal sgstAmount,
            BigDecimal lineTotal
    ) {
        return new BillItem(
                id,
                billId,
                productId,
                batchId,
                quantity,
                sellingRate,
                discountAmount,
                taxableAmount,
                cgstRate,
                cgstAmount,
                sgstRate,
                sgstAmount,
                lineTotal
        );
    }

    private static void validateIds(
            Long productId,
            Long batchId
    ) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid product ID"
            );
        }

        if (batchId == null || batchId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid batch ID"
            );
        }
    }

    private static void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }
    }

    private static void validateSellingRate(BigDecimal sellingRate) {
        if (sellingRate == null
                || sellingRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Selling rate cannot be negative"
            );
        }
    }

    private static void validateDiscount(BigDecimal discountAmount) {
        if (discountAmount == null
                || discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Discount cannot be negative"
            );
        }
    }

    private static void validateTaxRate(
            BigDecimal rate,
            String taxName
    ) {
        if (rate == null
                || rate.compareTo(BigDecimal.ZERO) < 0
                || rate.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException(
                    taxName + " rate must be between 0 and 100"
            );
        }
    }

    public Long getId() {
        return id;
    }

    public Long getBillId() {
        return billId;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getBatchId() {
        return batchId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSellingRate() {
        return sellingRate;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getTaxableAmount() {
        return taxableAmount;
    }

    public BigDecimal getCgstRate() {
        return cgstRate;
    }

    public BigDecimal getCgstAmount() {
        return cgstAmount;
    }

    public BigDecimal getSgstRate() {
        return sgstRate;
    }

    public BigDecimal getSgstAmount() {
        return sgstAmount;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }
}
