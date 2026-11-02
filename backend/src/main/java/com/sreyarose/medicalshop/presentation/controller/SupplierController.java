package com.sreyarose.medicalshop.presentation.controller;

import com.sreyarose.medicalshop.application.usecase.CreateSupplierUsecase;
import com.sreyarose.medicalshop.application.usecase.GetSuppliersUsecase;
import com.sreyarose.medicalshop.application.usecase.UpdateSupplierStatusUsecase;
import com.sreyarose.medicalshop.domain.entity.Supplier;
import com.sreyarose.medicalshop.presentation.dto.SupplierRequest;
import com.sreyarose.medicalshop.presentation.dto.SupplierResponse;
import com.sreyarose.medicalshop.application.usecase.UpdateSupplierStatusUsecase;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final CreateSupplierUsecase createSupplierUsecase;
    private final GetSuppliersUsecase getSuppliersUsecase;
    private final UpdateSupplierStatusUsecase updateSupplierStatusUsecase;

    public SupplierController(
            CreateSupplierUsecase createSupplierUsecase,
            GetSuppliersUsecase getSuppliersUsecase,
            UpdateSupplierStatusUsecase updateSupplierStatusUsecase) {

        this.createSupplierUsecase = createSupplierUsecase;
        this.getSuppliersUsecase = getSuppliersUsecase;
        this.updateSupplierStatusUsecase = updateSupplierStatusUsecase;
    }

    @PostMapping
    public ResponseEntity<SupplierResponse> createSupplier(
            @Valid @RequestBody SupplierRequest request) {

        Supplier supplier = createSupplierUsecase.execute(
                request.getName(),
                request.getContactPerson(),
                request.getContactNumber(),
                request.getEmail(),
                request.getAddress()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SupplierResponse.from(supplier));
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponse>> getSuppliers(
            @RequestParam(defaultValue = "false") boolean activeOnly) {

        List<SupplierResponse> response =
                getSuppliersUsecase.execute(activeOnly)
                        .stream()
                        .map(SupplierResponse::from)
                        .toList();

        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<SupplierResponse> deactivateSupplier(
            @PathVariable Long id) {

        Supplier supplier = updateSupplierStatusUsecase.execute(id, false);

        return ResponseEntity.ok(SupplierResponse.from(supplier));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<SupplierResponse> activateSupplier(
            @PathVariable Long id) {

        Supplier supplier = updateSupplierStatusUsecase.execute(id, true);

        return ResponseEntity.ok(SupplierResponse.from(supplier));
    }
}