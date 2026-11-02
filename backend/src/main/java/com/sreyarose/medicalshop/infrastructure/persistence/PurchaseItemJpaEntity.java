package com.sreyarose.medicalshop.infrastructure.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "purchase_items")
public class PurchaseItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_id", nullable = false)
    private Long purchaseId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "batch_number", nullable = false, length = 100)
    private String batchNumber;

    @Column(name = "manufacturing_date", nullable = false)
    private LocalDate manufacturingDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "purchase_cost", nullable = false,
            precision = 12, scale = 2)
    private BigDecimal purchaseCost;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal mrp;

    @Column(name = "line_total", nullable = false,
            precision = 14, scale = 2)
    private BigDecimal lineTotal;

    public PurchaseItemJpaEntity() {
        // Required by JPA
    }

    public Long getId() {
        return id;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public LocalDate getManufacturingDate() {
        return manufacturingDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPurchaseCost() {
        return purchaseCost;
    }

    public BigDecimal getMrp() {
        return mrp;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public void setManufacturingDate(LocalDate manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPurchaseCost(BigDecimal purchaseCost) {
        this.purchaseCost = purchaseCost;
    }

    public void setMrp(BigDecimal mrp) {
        this.mrp = mrp;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
}