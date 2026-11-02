package com.sreyarose.medicalshop.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Purchase {

    private Long id;
    private Long supplierId;
    private String invoiceNumber;
    private LocalDate purchaseDate;
    private BigDecimal totalAmount;

    public Purchase(
            Long id,
            Long supplierId,
            String invoiceNumber,
            LocalDate purchaseDate,
            BigDecimal totalAmount) {

        if (supplierId == null || supplierId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid supplier ID");
        }

        if (invoiceNumber == null || invoiceNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Invoice number is required");
        }

        if (purchaseDate == null) {
            throw new IllegalArgumentException(
                    "Purchase date is required");
        }

        if (totalAmount == null
                || totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Total amount cannot be negative");
        }

        this.id = id;
        this.supplierId = supplierId;
        this.invoiceNumber = invoiceNumber.trim();
        this.purchaseDate = purchaseDate;
        this.totalAmount = totalAmount;
    }

    public Long getId() {
        return id;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}