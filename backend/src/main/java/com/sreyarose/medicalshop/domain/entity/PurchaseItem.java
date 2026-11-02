package com.sreyarose.medicalshop.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PurchaseItem {

    private Long id;
    private Long purchaseId;
    private Long productId;
    private String batchNumber;
    private LocalDate manufacturingDate;
    private LocalDate expiryDate;
    private int quantity;
    private BigDecimal purchaseCost;
    private BigDecimal mrp;
    private BigDecimal lineTotal;

    public PurchaseItem(
            Long id,
            Long purchaseId,
            Long productId,
            String batchNumber,
            LocalDate manufacturingDate,
            LocalDate expiryDate,
            int quantity,
            BigDecimal purchaseCost,
            BigDecimal mrp) {

        if (purchaseId != null && purchaseId <= 0) {
            throw new IllegalArgumentException("Invalid purchase ID");
        }

        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID");
        }

        if (batchNumber == null || batchNumber.isBlank()) {
            throw new IllegalArgumentException("Batch number is required");
        }

        if (manufacturingDate == null || expiryDate == null) {
            throw new IllegalArgumentException(
                    "Manufacturing and expiry dates are required");
        }

        if (manufacturingDate.isAfter(expiryDate)) {
            throw new IllegalArgumentException(
                    "Manufacturing date cannot be after expiry date");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        if (purchaseCost == null
                || purchaseCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Purchase cost cannot be negative");
        }

        if (mrp == null || mrp.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "MRP cannot be negative");
        }

        this.id = id;
        this.purchaseId = purchaseId;
        this.productId = productId;
        this.batchNumber = batchNumber.trim();
        this.manufacturingDate = manufacturingDate;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.purchaseCost = purchaseCost;
        this.mrp = mrp;
        this.lineTotal = purchaseCost.multiply(
                BigDecimal.valueOf(quantity));
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
}