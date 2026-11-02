
package com.sreyarose.medicalshop.presentation.dto;

import java.math.BigDecimal;

public class BillItemResponse {

    private final Long id;
    private final Long productId;
    private final String productName;
    private final Long batchId;
    private final String batchNumber;
    private final int quantity;
    private final BigDecimal sellingRate;
    private final BigDecimal discountAmount;

    private final BigDecimal taxableAmount;
    private final BigDecimal cgstRate;
    private final BigDecimal cgstAmount;
    private final BigDecimal sgstRate;
    private final BigDecimal sgstAmount;

    private final BigDecimal lineTotal;

    public BillItemResponse(
            Long id,
            Long productId,
            String productName,
            Long batchId,
            String batchNumber,
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
        this.productId = productId;
        this.productName = productName;
        this.batchId = batchId;
        this.batchNumber = batchNumber;
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

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Long getBatchId() {
        return batchId;
    }

    public String getBatchNumber() {
        return batchNumber;
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
    