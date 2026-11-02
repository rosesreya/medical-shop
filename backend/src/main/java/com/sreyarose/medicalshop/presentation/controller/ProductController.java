
package com.sreyarose.medicalshop.presentation.controller;

import java.math.BigDecimal;
import java.util.List;

import tools.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sreyarose.medicalshop.application.usecase.ChangeProductStatusUsecase;
import com.sreyarose.medicalshop.application.usecase.CreateProductUsecase;
import com.sreyarose.medicalshop.application.usecase.UpdateProductUsecase;
import com.sreyarose.medicalshop.domain.entity.Product;
import com.sreyarose.medicalshop.domain.repository.ProductRepository;
import com.sreyarose.medicalshop.presentation.dto.ProductResponse;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final CreateProductUsecase createProductUsecase;
    private final UpdateProductUsecase updateProductUsecase;
    private final ChangeProductStatusUsecase changeProductStatusUsecase;
    private final ProductRepository productRepository;

    public ProductController(
            CreateProductUsecase createProductUsecase,
            UpdateProductUsecase updateProductUsecase,
            ChangeProductStatusUsecase changeProductStatusUsecase,
            ProductRepository productRepository) {

        this.createProductUsecase = createProductUsecase;
        this.updateProductUsecase = updateProductUsecase;
        this.changeProductStatusUsecase = changeProductStatusUsecase;
        this.productRepository = productRepository;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @RequestBody JsonNode request) {

        String baseUnit = getOptionalText(
                request, "baseUnit", "UNIT");
        String packUnit = getOptionalText(
                request, "packUnit", "UNIT");
        int unitsPerPack = getOptionalInteger(
                request, "unitsPerPack", 1);
        BigDecimal cgstRate = getOptionalDecimal(
                request, "cgstRate", BigDecimal.ZERO);
        BigDecimal sgstRate = getOptionalDecimal(
                request, "sgstRate", BigDecimal.ZERO);

        Product product = createProductUsecase.execute(
                getRequiredText(request, "name"),
                getOptionalText(request, "brandName", null),
                getRequiredId(request, "categoryId"),
                getRequiredId(request, "manufacturerId"),
                getRequiredId(request, "genericNameId"),
                getOptionalText(request, "strength", null),
                getOptionalText(request, "dosageForm", null),
                getOptionalText(request, "description", null),
                baseUnit,
                packUnit,
                unitsPerPack,
                cgstRate,
                sgstRate
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(product));
    }

    @GetMapping
    public List<ProductResponse> getAll() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @RequestBody JsonNode request) {

        // Preserve existing values when older clients omit new fields.
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() ->
                        new com.sreyarose.medicalshop.application.exception
                                .ResourceNotFoundException(
                                        "Product not found"));

        String baseUnit = getOptionalText(
                request, "baseUnit", existingProduct.getBaseUnit());
        String packUnit = getOptionalText(
                request, "packUnit", existingProduct.getPackUnit());
        int unitsPerPack = getOptionalInteger(
                request, "unitsPerPack",
                existingProduct.getUnitsPerPack());
        BigDecimal cgstRate = getOptionalDecimal(
                request, "cgstRate", existingProduct.getCgstRate());
        BigDecimal sgstRate = getOptionalDecimal(
                request, "sgstRate", existingProduct.getSgstRate());

        Product product = updateProductUsecase.execute(
                id,
                getRequiredText(request, "name"),
                getOptionalText(request, "brandName", null),
                getRequiredId(request, "categoryId"),
                getRequiredId(request, "manufacturerId"),
                getRequiredId(request, "genericNameId"),
                getOptionalText(request, "strength", null),
                getOptionalText(request, "dosageForm", null),
                getOptionalText(request, "description", null),
                baseUnit,
                packUnit,
                unitsPerPack,
                cgstRate,
                sgstRate
        );

        return ResponseEntity.ok(toResponse(product));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        changeProductStatusUsecase.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        changeProductStatusUsecase.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getBrandName(),
                product.getCategoryId(),
                product.getManufacturerId(),
                product.getGenericNameId(),
                product.getStrength(),
                product.getDosageForm(),
                product.getDescription(),
                product.isActive(),
                product.getBaseUnit(),
                product.getPackUnit(),
                product.getUnitsPerPack(),
                product.getCgstRate(),
                product.getSgstRate()
        );
    }

    private String getRequiredText(
            JsonNode request, String field) {

        if (request == null
                || !request.isObject()
                || !request.has(field)
                || !request.get(field).isTextual()
                || request.get(field).asText().isBlank()) {

            throw new IllegalArgumentException(
                    field + " cannot be empty");
        }

        return request.get(field).asText().trim();
    }

    private String getOptionalText(
            JsonNode request,
            String field,
            String defaultValue) {

        if (request == null
                || !request.isObject()
                || !request.has(field)
                || request.get(field).isNull()) {
            return defaultValue;
        }

        if (!request.get(field).isTextual()) {
            throw new IllegalArgumentException(
                    field + " must be a string or null");
        }

        String value = request.get(field).asText();

        if (value.isBlank()
                && ("baseUnit".equals(field)
                || "packUnit".equals(field))) {
            throw new IllegalArgumentException(
                    field + " cannot be empty");
        }

        return value;
    }

    private Long getRequiredId(
            JsonNode request, String field) {

        if (request == null
                || !request.isObject()
                || !request.has(field)
                || !request.get(field).isIntegralNumber()
                || !request.get(field).canConvertToLong()
                || request.get(field).asLong() <= 0) {

            throw new IllegalArgumentException(
                    "A valid " + field + " is required");
        }

        return request.get(field).asLong();
    }

    private int getOptionalInteger(
            JsonNode request,
            String field,
            int defaultValue) {

        if (request == null
                || !request.isObject()
                || !request.has(field)) {
            return defaultValue;
        }

        JsonNode value = request.get(field);

        if (value == null
                || !value.isIntegralNumber()
                || !value.canConvertToInt()) {
            throw new IllegalArgumentException(
                    field + " must be an integer");
        }

        int result = value.asInt();

        if (result < 1) {
            throw new IllegalArgumentException(
                    field + " must be at least 1");
        }

        return result;
    }

    private BigDecimal getOptionalDecimal(
            JsonNode request,
            String field,
            BigDecimal defaultValue) {

        if (request == null
                || !request.isObject()
                || !request.has(field)) {
            return defaultValue;
        }

        JsonNode value = request.get(field);

        if (value == null || !value.isNumber()) {
            throw new IllegalArgumentException(
                    field + " must be a number");
        }

        BigDecimal result = value.decimalValue();

        if (result.compareTo(BigDecimal.ZERO) < 0
                || result.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException(
                    field + " must be between 0 and 100");
        }

        return result;
    }
}
