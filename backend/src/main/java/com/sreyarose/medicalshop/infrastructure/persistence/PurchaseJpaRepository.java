package com.sreyarose.medicalshop.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseJpaRepository
        extends JpaRepository<PurchaseJpaEntity, Long> {

    Optional<PurchaseJpaEntity> findBySupplierIdAndInvoiceNumber(
            Long supplierId,
            String invoiceNumber
    );

    boolean existsBySupplierIdAndInvoiceNumber(
            Long supplierId,
            String invoiceNumber
    );
}