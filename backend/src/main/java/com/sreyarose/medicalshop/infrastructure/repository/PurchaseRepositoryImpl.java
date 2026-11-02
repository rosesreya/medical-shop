package com.sreyarose.medicalshop.infrastructure.repository;

import com.sreyarose.medicalshop.domain.entity.Purchase;
import com.sreyarose.medicalshop.domain.repository.PurchaseRepository;
import com.sreyarose.medicalshop.infrastructure.mapper.PurchaseMapper;
import com.sreyarose.medicalshop.infrastructure.persistence.PurchaseJpaEntity;
import com.sreyarose.medicalshop.infrastructure.persistence.PurchaseJpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PurchaseRepositoryImpl implements PurchaseRepository {

    private final PurchaseJpaRepository purchaseJpaRepository;

    public PurchaseRepositoryImpl(
            PurchaseJpaRepository purchaseJpaRepository) {
        this.purchaseJpaRepository = purchaseJpaRepository;
    }

    @Override
    public Purchase save(Purchase purchase) {
        PurchaseJpaEntity entity =
                PurchaseMapper.toJpaEntity(purchase);

        PurchaseJpaEntity savedEntity =
                purchaseJpaRepository.save(entity);

        return PurchaseMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Purchase> findById(Long id) {
        return purchaseJpaRepository.findById(id)
                .map(PurchaseMapper::toDomain);
    }

    @Override
    public List<Purchase> findAll() {
        return purchaseJpaRepository.findAll()
                .stream()
                .map(PurchaseMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsBySupplierIdAndInvoiceNumber(
            Long supplierId,
            String invoiceNumber) {

        return purchaseJpaRepository
                .existsBySupplierIdAndInvoiceNumber(
                        supplierId,
                        invoiceNumber.trim()
                );
    }
}