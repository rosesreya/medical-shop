package com.sreyarose.medicalshop.application.usecase;

import com.sreyarose.medicalshop.application.exception.ResourceNotFoundException;
import com.sreyarose.medicalshop.domain.entity.Supplier;
import com.sreyarose.medicalshop.domain.repository.SupplierRepository;

import org.springframework.stereotype.Service;

@Service
public class UpdateSupplierStatusUsecase {

    private final SupplierRepository supplierRepository;

    public UpdateSupplierStatusUsecase(
            SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public Supplier execute(Long id, boolean active) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Supplier not found with ID: " + id));

        if (active) {
            supplier.activate();
        } else {
            supplier.deactivate();
        }

        return supplierRepository.save(supplier);
    }
}