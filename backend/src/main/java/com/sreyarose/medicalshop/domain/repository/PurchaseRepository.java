package com.sreyarose.medicalshop.domain.repository;

import com.sreyarose.medicalshop.domain.entity.Purchase;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository {

    Purchase save(Purchase purchase);

    Optional<Purchase> findById(Long id);

    List<Purchase> findAll();

    boolean existsBySupplierIdAndInvoiceNumber(
            Long supplierId,
            String invoiceNumber
    );
}