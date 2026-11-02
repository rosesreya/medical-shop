
package com.sreyarose.medicalshop.presentation.controller;

import com.sreyarose.medicalshop.application.exception.ResourceNotFoundException;
import com.sreyarose.medicalshop.application.usecase.CreatePurchaseUsecase;
import com.sreyarose.medicalshop.domain.entity.Purchase;
import com.sreyarose.medicalshop.domain.entity.PurchaseItem;
import com.sreyarose.medicalshop.domain.repository.ProductRepository;
import com.sreyarose.medicalshop.domain.repository.PurchaseItemRepository;
import com.sreyarose.medicalshop.domain.repository.PurchaseRepository;
import com.sreyarose.medicalshop.domain.repository.SupplierRepository;
import com.sreyarose.medicalshop.presentation.dto.PurchaseItemRequest;
import com.sreyarose.medicalshop.presentation.dto.PurchaseItemResponse;
import com.sreyarose.medicalshop.presentation.dto.PurchaseRequest;
import com.sreyarose.medicalshop.presentation.dto.PurchaseResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final CreatePurchaseUsecase createPurchaseUsecase;
    private final PurchaseItemRepository purchaseItemRepository;
    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public PurchaseController(
            CreatePurchaseUsecase createPurchaseUsecase,
            PurchaseItemRepository purchaseItemRepository,
            PurchaseRepository purchaseRepository,
            ProductRepository productRepository,
            SupplierRepository supplierRepository) {

        this.createPurchaseUsecase = createPurchaseUsecase;
        this.purchaseItemRepository = purchaseItemRepository;
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
    }

    // POST: Create a purchase
    @PostMapping
    public ResponseEntity<PurchaseResponse> createPurchase(
            @RequestBody PurchaseRequest request) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one purchase item is required");
        }

        List<PurchaseItem> items = request.getItems()
                .stream()
                .map(this::toDomain)
                .toList();

        Purchase purchase = createPurchaseUsecase.execute(
                request.getSupplierId(),
                request.getInvoiceNumber(),
                request.getPurchaseDate(),
                items
        );

        PurchaseResponse response = toPurchaseResponse(purchase);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET: Retrieve all purchases
    @GetMapping
    public ResponseEntity<List<PurchaseResponse>> getAllPurchases() {

        List<PurchaseResponse> purchases = purchaseRepository.findAll()
                .stream()
                .map(this::toPurchaseResponse)
                .toList();

        return ResponseEntity.ok(purchases);
    }

    // GET: Retrieve a purchase by ID
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponse> getPurchaseById(
            @PathVariable Long id) {

        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Purchase not found with ID: " + id));

        return ResponseEntity.ok(toPurchaseResponse(purchase));
    }

    // Convert request DTO to domain entity
    private PurchaseItem toDomain(PurchaseItemRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Purchase item cannot be null");
        }

        return new PurchaseItem(
                null,
                null,
                request.getProductId(),
                request.getBatchNumber(),
                request.getManufacturingDate(),
                request.getExpiryDate(),
                request.getQuantity(),
                request.getPurchaseCost(),
                request.getMrp()
        );
    }

    // Convert a purchase item into a response with its product name
    private PurchaseItemResponse toItemResponse(PurchaseItem item) {

        String productName = productRepository
                .findById(item.getProductId())
                .map(product -> product.getName())
                .orElse("Unknown product");

        return PurchaseItemResponse.from(item, productName);
    }

    // Convert a purchase into a response with supplier and item details
    private PurchaseResponse toPurchaseResponse(Purchase purchase) {

        String supplierName = supplierRepository
                .findById(purchase.getSupplierId())
                .map(supplier -> supplier.getName())
                .orElse("Unknown supplier");

        List<PurchaseItemResponse> items =
                purchaseItemRepository
                        .findByPurchaseId(purchase.getId())
                        .stream()
                        .map(this::toItemResponse)
                        .toList();

        return PurchaseResponse.from(
                purchase,
                supplierName,
                items
        );
    }
}
