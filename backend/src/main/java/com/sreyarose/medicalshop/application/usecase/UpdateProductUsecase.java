
package com.sreyarose.medicalshop.application.usecase;

import org.springframework.stereotype.Service;

import com.sreyarose.medicalshop.application.exception.ResourceNotFoundException;
import java.math.BigDecimal;

import com.sreyarose.medicalshop.domain.entity.Category;
import com.sreyarose.medicalshop.domain.entity.Manufacturer;
import com.sreyarose.medicalshop.domain.entity.GenericName;
import com.sreyarose.medicalshop.domain.entity.Product;

import com.sreyarose.medicalshop.domain.repository.CategoryRepository;
import com.sreyarose.medicalshop.domain.repository.ManufacturerRepository;
import com.sreyarose.medicalshop.domain.repository.GenericNameRepository;
import com.sreyarose.medicalshop.domain.repository.ProductRepository;

@Service
public class UpdateProductUsecase {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final GenericNameRepository genericNameRepository;

    public UpdateProductUsecase(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ManufacturerRepository manufacturerRepository,
            GenericNameRepository genericNameRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.genericNameRepository = genericNameRepository;
    }

    
public Product execute(
        Long id,
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

    Product existingProduct = productRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Product not found"));

    if (name == null || name.isBlank()) {
        throw new IllegalArgumentException(
                "Product name cannot be empty");
    }

    name = name.trim();

    if (!existingProduct.getName().equalsIgnoreCase(name)
            && productRepository.existsByNameIgnoreCase(name)) {
        throw new IllegalArgumentException(
                "Product name already exists");
    }

    Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Category not found"));

    if (!category.isActive()) {
        throw new IllegalArgumentException(
                "Selected category is inactive");
    }

    Manufacturer manufacturer =
            manufacturerRepository.findById(manufacturerId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Manufacturer not found"));

    if (!manufacturer.isActive()) {
        throw new IllegalArgumentException(
                "Selected manufacturer is inactive");
    }

    GenericName genericName =
            genericNameRepository.findById(genericNameId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Generic name not found"));

    if (!genericName.isActive()) {
        throw new IllegalArgumentException(
                "Selected generic name is inactive");
    }

    existingProduct.setName(name);
    existingProduct.setBrandName(brandName);
    existingProduct.setCategoryId(categoryId);
    existingProduct.setManufacturerId(manufacturerId);
    existingProduct.setGenericNameId(genericNameId);
    existingProduct.setStrength(strength);
    existingProduct.setDosageForm(dosageForm);
    existingProduct.setDescription(description);

    existingProduct.setBaseUnit(baseUnit);
    existingProduct.setPackUnit(packUnit);
    existingProduct.setUnitsPerPack(unitsPerPack);
    existingProduct.setCgstRate(cgstRate);
    existingProduct.setSgstRate(sgstRate);

    return productRepository.save(existingProduct);
}

}
