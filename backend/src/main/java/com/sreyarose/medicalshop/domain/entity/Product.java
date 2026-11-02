
package com.sreyarose.medicalshop.domain.entity;

import java.math.BigDecimal;

public class Product {

    private Long id;
    private String name;
    private String brandName;
    private Long categoryId;
    private Long manufacturerId;
    private Long genericNameId;
    private String strength;
    private String dosageForm;
    private String description;
    private boolean active;

    // Unit and GST configuration
    private String baseUnit;
    private String packUnit;
    private int unitsPerPack;
    private BigDecimal cgstRate;
    private BigDecimal sgstRate;

    /**
     * Backward-compatible constructor.
     * Existing code can continue using this constructor.
     */
    public Product(
            Long id,
            String name,
            String brandName,
            Long categoryId,
            Long manufacturerId,
            Long genericNameId,
            String strength,
            String dosageForm,
            String description,
            boolean active) {

        this(
                id,
                name,
                brandName,
                categoryId,
                manufacturerId,
                genericNameId,
                strength,
                dosageForm,
                description,
                active,
                "UNIT",
                "UNIT",
                1,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    /**
     * Constructor with unit and GST configuration.
     */
    public Product(
            Long id,
            String name,
            String brandName,
            Long categoryId,
            Long manufacturerId,
            Long genericNameId,
            String strength,
            String dosageForm,
            String description,
            boolean active,
            String baseUnit,
            String packUnit,
            int unitsPerPack,
            BigDecimal cgstRate,
            BigDecimal sgstRate) {

        this.id = id;

        setName(name);
        setBrandName(brandName);
        setCategoryId(categoryId);
        setManufacturerId(manufacturerId);
        setGenericNameId(genericNameId);
        setStrength(strength);
        setDosageForm(dosageForm);
        setDescription(description);

        setBaseUnit(baseUnit);
        setPackUnit(packUnit);
        setUnitsPerPack(unitsPerPack);
        setCgstRate(cgstRate);
        setSgstRate(sgstRate);

        this.active = active;
    }

    /**
     * Constructor for creating a new product
     * with default unit and GST settings.
     */
    public Product(
            String name,
            String brandName,
            Long categoryId,
            Long manufacturerId,
            Long genericNameId,
            String strength,
            String dosageForm,
            String description) {

        this(
                null,
                name,
                brandName,
                categoryId,
                manufacturerId,
                genericNameId,
                strength,
                dosageForm,
                description,
                true
        );
    }

    /**
     * Constructor for creating a new product
     * with unit and GST configuration.
     */
    public Product(
            String name,
            String brandName,
            Long categoryId,
            Long manufacturerId,
            Long genericNameId,
            String strength,
            String dosageForm,
            String description,
            String baseUnit,
            String packUnit,
            int unitsPerPack,
            BigDecimal cgstRate,
            BigDecimal sgstRate) {

        this(
                null,
                name,
                brandName,
                categoryId,
                manufacturerId,
                genericNameId,
                strength,
                dosageForm,
                description,
                true,
                baseUnit,
                packUnit,
                unitsPerPack,
                cgstRate,
                sgstRate
        );
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Product name cannot be empty");
        }

        this.name = name.trim();
    }

    public void setBrandName(String brandName) {
        this.brandName = normalizeOptionalField(brandName);
    }

    public void setCategoryId(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException(
                    "A valid category is required");
        }

        this.categoryId = categoryId;
    }

    public void setManufacturerId(Long manufacturerId) {
        if (manufacturerId == null || manufacturerId <= 0) {
            throw new IllegalArgumentException(
                    "A valid manufacturer is required");
        }

        this.manufacturerId = manufacturerId;
    }

    public void setGenericNameId(Long genericNameId) {
        if (genericNameId == null || genericNameId <= 0) {
            throw new IllegalArgumentException(
                    "A valid generic name is required");
        }

        this.genericNameId = genericNameId;
    }

    public void setStrength(String strength) {
        this.strength = normalizeOptionalField(strength);
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = normalizeOptionalField(dosageForm);
    }

    public void setDescription(String description) {
        this.description = normalizeOptionalField(description);
    }

    public void setBaseUnit(String baseUnit) {
        if (baseUnit == null || baseUnit.isBlank()) {
            throw new IllegalArgumentException(
                    "Base unit cannot be empty");
        }

        this.baseUnit = baseUnit.trim().toUpperCase();
    }

    public void setPackUnit(String packUnit) {
        if (packUnit == null || packUnit.isBlank()) {
            throw new IllegalArgumentException(
                    "Pack unit cannot be empty");
        }

        this.packUnit = packUnit.trim().toUpperCase();
    }

    public void setUnitsPerPack(int unitsPerPack) {
        if (unitsPerPack < 1) {
            throw new IllegalArgumentException(
                    "Units per pack must be at least 1");
        }

        this.unitsPerPack = unitsPerPack;
    }

    public void setCgstRate(BigDecimal cgstRate) {
        this.cgstRate = validateTaxRate(cgstRate, "CGST");
    }

    public void setSgstRate(BigDecimal sgstRate) {
        this.sgstRate = validateTaxRate(sgstRate, "SGST");
    }

    private BigDecimal validateTaxRate(
            BigDecimal rate,
            String taxName) {

        if (rate == null
                || rate.compareTo(BigDecimal.ZERO) < 0
                || rate.compareTo(new BigDecimal("100")) > 0) {

            throw new IllegalArgumentException(
                    taxName + " rate must be between 0 and 100");
        }

        return rate;
    }

    private String normalizeOptionalField(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
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

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }
}
