package com.sreyarose.medicalshop.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Batch {

    private Long id;
    private Long productId;
    private String batchNumber;
    private LocalDate manufacturingDate;
    private LocalDate expiryDate;
    private int quantity;
    private BigDecimal mrp;

    public Batch(
            Long id,
            Long productId,
            String batchNumber,
            LocalDate manufacturingDate,
            LocalDate expiryDate,
            int quantity,
            BigDecimal mrp) {

        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID");
        }

        if (batchNumber == null || batchNumber.isBlank()) {
            throw new IllegalArgumentException("Batch number is required");
        }

        if (manufacturingDate == null || expiryDate == null) {
            throw new IllegalArgumentException("Manufacturing and expiry dates are required");
        }

        if (manufacturingDate.isAfter(expiryDate)) {
            throw new IllegalArgumentException(
                    "Manufacturing date cannot be after expiry date");
        }

        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        if (mrp == null || mrp.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("MRP cannot be negative");
        }

        this.id = id;
        this.productId = productId;
        this.batchNumber = batchNumber.trim();
        this.manufacturingDate = manufacturingDate;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.mrp = mrp;
    }

    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Received quantity must be greater than zero");
        }

        this.quantity += quantity;
    }

    public void deductStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Deduction quantity must be greater than zero");
        }

        if (quantity > this.quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        this.quantity -= quantity;
    }

    public Long getId() {
        return id;
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

    public BigDecimal getMrp() {
        return mrp;
    }
}