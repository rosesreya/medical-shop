package com.sreyarose.medicalshop.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "batches",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_batches_product_batch_number",
            columnNames = {"product_id", "batch_number"}
        )
    }
)
public class BatchJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal mrp;

    public BatchJpaEntity() {
        // Required by JPA
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

    public void setId(Long id) {
        this.id = id;
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

    public void setMrp(BigDecimal mrp) {
        this.mrp = mrp;
    }
}