
package com.sreyarose.medicalshop.application.usecase;

import org.springframework.stereotype.Service;

import com.sreyarose.medicalshop.domain.entity.Category;
import com.sreyarose.medicalshop.domain.entity.Manufacturer;
import com.sreyarose.medicalshop.domain.entity.GenericName;
import com.sreyarose.medicalshop.domain.entity.Product;

import com.sreyarose.medicalshop.domain.repository.CategoryRepository;
import com.sreyarose.medicalshop.domain.repository.ManufacturerRepository;
import com.sreyarose.medicalshop.domain.repository.GenericNameRepository;
import com.sreyarose.medicalshop.domain.repository.ProductRepository;

import com.sreyarose.medicalshop.application.exception.ResourceNotFoundException;
import java.math.BigDecimal;

@Service
public class CreateProductUsecase {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final GenericNameRepository genericNameRepository;

    public CreateProductUsecase(
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
        java.math.BigDecimal cgstRate,
        java.math.BigDecimal sgstRate) {

    if (productRepository.existsByNameIgnoreCase(name.trim())) {
        throw new IllegalArgumentException(
                "Product name already exists");
    }

    Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Category not found"));

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

    Product product = new Product(
            name,
            brandName,
            categoryId,
            manufacturerId,
            genericNameId,
            strength,
            dosageForm,
            description,
            baseUnit,
            packUnit,
            unitsPerPack,
            cgstRate,
            sgstRate
    );

    return productRepository.save(product);
}

}
