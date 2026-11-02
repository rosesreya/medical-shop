package com.sreyarose.medicalshop.presentation.dto;

import com.sreyarose.medicalshop.domain.entity.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PurchaseResponse {

    private Long id;
    private Long supplierId;
    private String invoiceNumber;
    private LocalDate purchaseDate;
    private BigDecimal totalAmount;
    private List<PurchaseItemResponse> items;
    private String supplierName;

    public PurchaseResponse(
        Long id,
        Long supplierId,
        String supplierName,
        String invoiceNumber,
        LocalDate purchaseDate,
        BigDecimal totalAmount,
        List<PurchaseItemResponse> items) {

    this.id = id;
    this.supplierId = supplierId;
    this.supplierName = supplierName;
    this.invoiceNumber = invoiceNumber;
    this.purchaseDate = purchaseDate;
    this.totalAmount = totalAmount;
    this.items = items;
}

    public static PurchaseResponse from(
        Purchase purchase,
        String supplierName,
        List<PurchaseItemResponse> items) {

    return new PurchaseResponse(
            purchase.getId(),
            purchase.getSupplierId(),
            supplierName,
            purchase.getInvoiceNumber(),
            purchase.getPurchaseDate(),
            purchase.getTotalAmount(),
            items
    );
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

    public List<PurchaseItemResponse> getItems() {
        return items;
    }
    public String getSupplierName() {
        return supplierName;
    }
}
