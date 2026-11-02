package com.sreyarose.medicalshop.application.usecase;

import com.sreyarose.medicalshop.domain.entity.Supplier;
import com.sreyarose.medicalshop.domain.repository.SupplierRepository;

import org.springframework.stereotype.Service;

@Service
public class CreateSupplierUsecase {

    private final SupplierRepository supplierRepository;

    public CreateSupplierUsecase(
            SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public Supplier execute(
            String name,
            String contactPerson,
            String contactNumber,
            String email,
            String address) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Supplier name is required");
        }

        if (contactNumber == null || contactNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Supplier contact number is required");
        }

        String normalizedName = name.trim();

        if (supplierRepository.existsByNameIgnoreCase(
                normalizedName)) {
            throw new IllegalArgumentException(
                    "Supplier already exists");
        }

        Supplier supplier = new Supplier(
                null,
                normalizedName,
                contactPerson,
                contactNumber,
                email,
                address,
                true
        );

        return supplierRepository.save(supplier);
    }
}