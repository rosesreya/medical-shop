
package com.sreyarose.medicalshop.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bill {

    private final Long id;
    private final String billNumber;
    private final LocalDateTime billDate;
    private final String customerName;
    private final String customerMobile;

    private final BigDecimal subtotal;
    private final BigDecimal discountAmount;
    private final BigDecimal grandTotal;

    private final BigDecimal totalTaxableAmount;
    private final BigDecimal totalCgstAmount;
    private final BigDecimal totalSgstAmount;

    private final PaymentMethod paymentMethod;
    private final PaymentStatus paymentStatus;
    private final BigDecimal amountReceived;
    private final BigDecimal changeAmount;

    // Backward-compatible constructor.
    // Existing callers continue to work with zero tax totals.
    public Bill(
            Long id,
            String billNumber,
            LocalDateTime billDate,
            String customerName,
            String customerMobile,
            BigDecimal subtotal,
            BigDecimal discountAmount,
            BigDecimal grandTotal,
            PaymentMethod paymentMethod,
            PaymentStatus paymentStatus,
            BigDecimal amountReceived,
            BigDecimal changeAmount
    ) {
        this(
                id,
                billNumber,
                billDate,
                customerName,
                customerMobile,
                subtotal,
                discountAmount,
                grandTotal,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                paymentMethod,
                paymentStatus,
                amountReceived,
                changeAmount
        );
    }

    // Constructor for bills with GST totals.
    public Bill(
            Long id,
            String billNumber,
            LocalDateTime billDate,
            String customerName,
            String customerMobile,
            BigDecimal subtotal,
            BigDecimal discountAmount,
            BigDecimal grandTotal,
            BigDecimal totalTaxableAmount,
            BigDecimal totalCgstAmount,
            BigDecimal totalSgstAmount,
            PaymentMethod paymentMethod,
            PaymentStatus paymentStatus,
            BigDecimal amountReceived,
            BigDecimal changeAmount
    ) {
        if (billNumber == null || billNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Bill number is required"
            );
        }

        if (billDate == null) {
            throw new IllegalArgumentException(
                    "Bill date is required"
            );
        }

        requireNonNegative(subtotal, "Subtotal");
        requireNonNegative(discountAmount, "Discount");
        requireNonNegative(grandTotal, "Grand total");
        requireNonNegative(
                totalTaxableAmount, "Total taxable amount"
        );
        requireNonNegative(totalCgstAmount, "Total CGST");
        requireNonNegative(totalSgstAmount, "Total SGST");
        requireNonNegative(amountReceived, "Amount received");
        requireNonNegative(changeAmount, "Change amount");

        if (paymentMethod == null) {
            throw new IllegalArgumentException(
                    "Payment method is required"
            );
        }

        if (paymentStatus == null) {
            throw new IllegalArgumentException(
                    "Payment status is required"
            );
        }

        if (discountAmount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException(
                    "Discount cannot exceed subtotal"
            );
        }

        BigDecimal expectedTotal = subtotal.subtract(discountAmount);

        if (grandTotal.compareTo(expectedTotal) != 0) {
            throw new IllegalArgumentException(
                    "Grand total does not match subtotal and discount"
            );
        }

        if (paymentStatus == PaymentStatus.PAID
                && amountReceived.compareTo(grandTotal) < 0) {
            throw new IllegalArgumentException(
                    "Amount received is insufficient"
            );
        }

        if (paymentMethod != PaymentMethod.CASH
                && changeAmount.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException(
                    "Change is only applicable to cash payments"
            );
        }

        this.id = id;
        this.billNumber = billNumber;
        this.billDate = billDate;
        this.customerName = normalizeOptional(customerName);
        this.customerMobile = normalizeOptional(customerMobile);

        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.grandTotal = grandTotal;

        this.totalTaxableAmount = totalTaxableAmount;
        this.totalCgstAmount = totalCgstAmount;
        this.totalSgstAmount = totalSgstAmount;

        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.amountReceived = amountReceived;
        this.changeAmount = changeAmount;
    }

    private static void requireNonNegative(
            BigDecimal value,
            String fieldName
    ) {
        if (value == null
                || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be negative or null"
            );
        }
    }

    private static String normalizeOptional(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }

    public Long getId() {
        return id;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public BigDecimal getTotalTaxableAmount() {
        return totalTaxableAmount;
    }

    public BigDecimal getTotalCgstAmount() {
        return totalCgstAmount;
    }

    public BigDecimal getTotalSgstAmount() {
        return totalSgstAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public BigDecimal getAmountReceived() {
        return amountReceived;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }
}
