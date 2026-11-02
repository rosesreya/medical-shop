
package com.sreyarose.medicalshop.infrastructure.persistence;

import com.sreyarose.medicalshop.domain.entity.PaymentMethod;
import com.sreyarose.medicalshop.domain.entity.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "bills",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_bills_bill_number",
            columnNames = "bill_number"
        )
    }
)
public class BillJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_number", nullable = false, length = 50)
    private String billNumber;

    @Column(name = "bill_date", nullable = false)
    private LocalDateTime billDate;

    @Column(name = "customer_name", length = 150)
    private String customerName;

    @Column(name = "customer_mobile", length = 20)
    private String customerMobile;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "grand_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal grandTotal;

    @Column(
        name = "total_taxable_amount",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal totalTaxableAmount;

    @Column(
        name = "total_cgst_amount",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal totalCgstAmount;

    @Column(
        name = "total_sgst_amount",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal totalSgstAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus;

    @Column(name = "amount_received", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountReceived;

    @Column(name = "change_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal changeAmount;

    public BillJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDateTime billDate) {
        this.billDate = billDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public BigDecimal getTotalTaxableAmount() {
        return totalTaxableAmount;
    }

    public void setTotalTaxableAmount(BigDecimal totalTaxableAmount) {
        this.totalTaxableAmount = totalTaxableAmount;
    }

    public BigDecimal getTotalCgstAmount() {
        return totalCgstAmount;
    }

    public void setTotalCgstAmount(BigDecimal totalCgstAmount) {
        this.totalCgstAmount = totalCgstAmount;
    }

    public BigDecimal getTotalSgstAmount() {
        return totalSgstAmount;
    }

    public void setTotalSgstAmount(BigDecimal totalSgstAmount) {
        this.totalSgstAmount = totalSgstAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getAmountReceived() {
        return amountReceived;
    }

    public void setAmountReceived(BigDecimal amountReceived) {
        this.amountReceived = amountReceived;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }
}
