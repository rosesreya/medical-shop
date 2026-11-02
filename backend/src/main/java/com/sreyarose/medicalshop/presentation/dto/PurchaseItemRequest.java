package com.sreyarose.medicalshop.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PurchaseItemRequest {

    private Long productId;
    private String batchNumber;
    private LocalDate manufacturingDate;
    private LocalDate expiryDate;
    private int quantity;
    private BigDecimal purchaseCost;
    private BigDecimal mrp;

    public PurchaseItemRequest() {
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
}