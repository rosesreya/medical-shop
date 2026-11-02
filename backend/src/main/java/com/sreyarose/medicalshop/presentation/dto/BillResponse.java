
package com.sreyarose.medicalshop.presentation.dto;

import com.sreyarose.medicalshop.domain.entity.PaymentMethod;
import com.sreyarose.medicalshop.domain.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BillResponse {

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
    private final List<BillItemResponse> items;

    // Backward-compatible constructor.
    public BillResponse(
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
            BigDecimal changeAmount,
            List<BillItemResponse> items
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
                changeAmount,
                items
        );
    }

    // Constructor including bill-level GST totals.
    public BillResponse(
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
            BigDecimal changeAmount,
            List<BillItemResponse> items
    ) {
        this.id = id;
        this.billNumber = billNumber;
        this.billDate = billDate;
        this.customerName = customerName;
        this.customerMobile = customerMobile;

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

        this.items = List.copyOf(items);
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

    public List<BillItemResponse> getItems() {
        return items;
    }
}
