
package com.sreyarose.medicalshop.infrastructure.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "manufacturer_id", nullable = false)
    private Long manufacturerId;

    @Column(name = "generic_name_id", nullable = false)
    private Long genericNameId;

    private String strength;

    @Column(name = "dosage_form")
    private String dosageForm;

    private String description;

    @Column(nullable = false)
    private boolean active = true;

    // Unit configuration
    @Column(name = "base_unit", nullable = false, length = 20)
    private String baseUnit = "UNIT";

    @Column(name = "pack_unit", nullable = false, length = 20)
    private String packUnit = "UNIT";

    @Column(name = "units_per_pack", nullable = false)
    private int unitsPerPack = 1;

    // GST configuration
    @Column(
        name = "cgst_rate",
        nullable = false,
        precision = 5,
        scale = 2
    )
    private BigDecimal cgstRate = BigDecimal.ZERO;

    @Column(
        name = "sgst_rate",
        nullable = false,
        precision = 5,
        scale = 2
    )
    private BigDecimal sgstRate = BigDecimal.ZERO;

    protected ProductJpaEntity() {
        // Required by JPA
    }

    /**
     * Existing constructor retained for backward compatibility.
     */
    public ProductJpaEntity(
            String name,
            String brandName,
            Long categoryId,
            Long manufacturerId,
            Long genericNameId,
            String strength,
            String dosageForm,
            String description,
            boolean active) {

        this.name = name;
        this.brandName = brandName;
        this.categoryId = categoryId;
        this.manufacturerId = manufacturerId;
        this.genericNameId = genericNameId;
        this.strength = strength;
        this.dosageForm = dosageForm;
        this.description = description;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrandName() {
        return brandName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getManufacturerId() {
        return manufacturerId;
    }

    public Long getGenericNameId() {
        return genericNameId;
    }

    public String getStrength() {
        return strength;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public String getPackUnit() {
        return packUnit;
    }

    public int getUnitsPerPack() {
        return unitsPerPack;
    }

    public BigDecimal getCgstRate() {
        return cgstRate;
    }

    public BigDecimal getSgstRate() {
        return sgstRate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setManufacturerId(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public void setGenericNameId(Long genericNameId) {
        this.genericNameId = genericNameId;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setBaseUnit(String baseUnit) {
        this.baseUnit = baseUnit;
    }

    public void setPackUnit(String packUnit) {
        this.packUnit = packUnit;
    }

    public void setUnitsPerPack(int unitsPerPack) {
        this.unitsPerPack = unitsPerPack;
    }

    public void setCgstRate(BigDecimal cgstRate) {
        this.cgstRate = cgstRate;
    }

    public void setSgstRate(BigDecimal sgstRate) {
        this.sgstRate = sgstRate;
    }
}
