package com.sreyarose.medicalshop.infrastructure.repository;

import com.sreyarose.medicalshop.domain.entity.PurchaseItem;
import com.sreyarose.medicalshop.domain.repository.PurchaseItemRepository;
import com.sreyarose.medicalshop.infrastructure.mapper.PurchaseItemMapper;
import com.sreyarose.medicalshop.infrastructure.persistence.PurchaseItemJpaEntity;
import com.sreyarose.medicalshop.infrastructure.persistence.PurchaseItemJpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PurchaseItemRepositoryImpl
        implements PurchaseItemRepository {

    private final PurchaseItemJpaRepository purchaseItemJpaRepository;

    public PurchaseItemRepositoryImpl(
            PurchaseItemJpaRepository purchaseItemJpaRepository) {
        this.purchaseItemJpaRepository = purchaseItemJpaRepository;
    }

    @Override
    public PurchaseItem save(PurchaseItem item) {
        PurchaseItemJpaEntity entity =
                PurchaseItemMapper.toJpaEntity(item);

        PurchaseItemJpaEntity savedEntity =
                purchaseItemJpaRepository.save(entity);

        return PurchaseItemMapper.toDomain(savedEntity);
    }

    @Override
    public List<PurchaseItem> saveAll(List<PurchaseItem> items) {
        List<PurchaseItemJpaEntity> entities = items.stream()
                .map(PurchaseItemMapper::toJpaEntity)
                .toList();

        return purchaseItemJpaRepository.saveAll(entities)
                .stream()
                .map(PurchaseItemMapper::toDomain)
                .toList();
    }

    @Override
    public List<PurchaseItem> findByPurchaseId(Long purchaseId) {
        return purchaseItemJpaRepository
                .findByPurchaseId(purchaseId)
                .stream()
                .map(PurchaseItemMapper::toDomain)
                .toList();
    }
}