package com.sreyarose.medicalshop.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseItemJpaRepository
        extends JpaRepository<PurchaseItemJpaEntity, Long> {

    List<PurchaseItemJpaEntity> findByPurchaseId(Long purchaseId);
}