package com.sreyarose.medicalshop.application.usecase;

import com.sreyarose.medicalshop.domain.entity.Supplier;
import com.sreyarose.medicalshop.domain.repository.SupplierRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetSuppliersUsecase {

    private final SupplierRepository supplierRepository;

    public GetSuppliersUsecase(
            SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> execute(boolean activeOnly) {

        if (activeOnly) {
            return supplierRepository.findAllActive();
        }

        return supplierRepository.findAll();
    }
}