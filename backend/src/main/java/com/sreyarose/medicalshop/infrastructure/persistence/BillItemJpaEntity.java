
package com.sreyarose.medicalshop.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bill_items")
public class BillItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_id", nullable = false)
    private Long billId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "selling_rate", nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingRate;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "taxable_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxableAmount;

    @Column(name = "cgst_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal cgstRate;

    @Column(name = "cgst_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal cgstAmount;

    @Column(name = "sgst_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal sgstRate;

    @Column(name = "sgst_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal sgstAmount;

    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    public BillItemJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSellingRate() {
        return sellingRate;
    }

    public void setSellingRate(BigDecimal sellingRate) {
        this.sellingRate = sellingRate;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTaxableAmount() {
        return taxableAmount;
    }

    public void setTaxableAmount(BigDecimal taxableAmount) {
        this.taxableAmount = taxableAmount;
    }

    public BigDecimal getCgstRate() {
        return cgstRate;
    }

    public void setCgstRate(BigDecimal cgstRate) {
        this.cgstRate = cgstRate;
    }

    public BigDecimal getCgstAmount() {
        return cgstAmount;
    }

    public void setCgstAmount(BigDecimal cgstAmount) {
        this.cgstAmount = cgstAmount;
    }

    public BigDecimal getSgstRate() {
        return sgstRate;
    }

    public void setSgstRate(BigDecimal sgstRate) {
        this.sgstRate = sgstRate;
    }

    public BigDecimal getSgstAmount() {
        return sgstAmount;
    }

    public void setSgstAmount(BigDecimal sgstAmount) {
        this.sgstAmount = sgstAmount;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
}
