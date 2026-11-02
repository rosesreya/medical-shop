
package com.sreyarose.medicalshop.presentation.dto;

import java.math.BigDecimal;

public class ProductResponse {

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

    private String baseUnit;
    private String packUnit;
    private int unitsPerPack;
    private BigDecimal cgstRate;
    private BigDecimal sgstRate;

    public ProductResponse(
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
        this.name = name;
        this.brandName = brandName;
        this.categoryId = categoryId;
        this.manufacturerId = manufacturerId;
        this.genericNameId = genericNameId;
        this.strength = strength;
        this.dosageForm = dosageForm;
        this.description = description;
        this.active = active;
        this.baseUnit = baseUnit;
        this.packUnit = packUnit;
        this.unitsPerPack = unitsPerPack;
        this.cgstRate = cgstRate;
        this.sgstRate = sgstRate;
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
}
