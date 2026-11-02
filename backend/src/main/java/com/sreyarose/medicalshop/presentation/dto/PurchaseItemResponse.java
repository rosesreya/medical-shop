
package com.sreyarose.medicalshop.presentation.dto;

import com.sreyarose.medicalshop.domain.entity.PurchaseItem;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PurchaseItemResponse {

    private Long id;
    private Long purchaseId;
    private Long productId;
    private String productName;
    private String batchNumber;
    private LocalDate manufacturingDate;
    private LocalDate expiryDate;
    private int quantity;
    private BigDecimal purchaseCost;
    private BigDecimal mrp;
    private BigDecimal lineTotal;

    public PurchaseItemResponse(
            Long id,
            Long purchaseId,
            Long productId,
            String productName,
            String batchNumber,
            LocalDate manufacturingDate,
            LocalDate expiryDate,
            int quantity,
            BigDecimal purchaseCost,
            BigDecimal mrp,
            BigDecimal lineTotal) {

        this.id = id;
        this.purchaseId = purchaseId;
        this.productId = productId;
        this.productName = productName;
        this.batchNumber = batchNumber;
        this.manufacturingDate = manufacturingDate;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.purchaseCost = purchaseCost;
        this.mrp = mrp;
        this.lineTotal = lineTotal;
    }

    public static PurchaseItemResponse from(
            PurchaseItem item,
            String productName) {

        return new PurchaseItemResponse(
                item.getId(),
                item.getPurchaseId(),
                item.getProductId(),
                productName,
                item.getBatchNumber(),
                item.getManufacturingDate(),
                item.getExpiryDate(),
                item.getQuantity(),
                item.getPurchaseCost(),
                item.getMrp(),
                item.getLineTotal()
        );
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

    public String getProductName() {
        return productName;
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
