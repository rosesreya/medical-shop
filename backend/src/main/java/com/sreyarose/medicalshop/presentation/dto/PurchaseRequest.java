package com.sreyarose.medicalshop.presentation.dto;

import java.time.LocalDate;
import java.util.List;

public class PurchaseRequest {

    private Long supplierId;
    private String invoiceNumber;
    private LocalDate purchaseDate;
    private List<PurchaseItemRequest> items;

    public PurchaseRequest() {
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

    public List<PurchaseItemRequest> getItems() {
        return items;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setItems(List<PurchaseItemRequest> items) {
        this.items = items;
    }
}