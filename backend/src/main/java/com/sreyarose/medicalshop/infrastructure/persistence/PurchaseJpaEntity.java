package com.sreyarose.medicalshop.infrastructure.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "purchases",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_purchases_supplier_invoice",
            columnNames = {"supplier_id", "invoice_number"}
        )
    }
)
public class PurchaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(name = "invoice_number", nullable = false, length = 100)
    private String invoiceNumber;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "total_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    public PurchaseJpaEntity() {
        // Required by JPA
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

    public void setId(Long id) {
        this.id = id;
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

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}