package com.sreyarose.medicalshop.infrastructure.mapper;    

import com.sreyarose.medicalshop.domain.entity.PurchaseItem;
import com.sreyarose.medicalshop.infrastructure.persistence.PurchaseItemJpaEntity;

public class PurchaseItemMapper {

    public static PurchaseItem toDomain(
            PurchaseItemJpaEntity entity) {

        if (entity == null) {
            return null;
        }

        return new PurchaseItem(
                entity.getId(),
                entity.getPurchaseId(),
                entity.getProductId(),
                entity.getBatchNumber(),
                entity.getManufacturingDate(),
                entity.getExpiryDate(),
                entity.getQuantity(),
                entity.getPurchaseCost(),
                entity.getMrp()
        );
    }

    public static PurchaseItemJpaEntity toJpaEntity(
            PurchaseItem item) {

        if (item == null) {
            return null;
        }

        PurchaseItemJpaEntity entity =
                new PurchaseItemJpaEntity();

        entity.setId(item.getId());
        entity.setPurchaseId(item.getPurchaseId());
        entity.setProductId(item.getProductId());
        entity.setBatchNumber(item.getBatchNumber());
        entity.setManufacturingDate(item.getManufacturingDate());
        entity.setExpiryDate(item.getExpiryDate());
        entity.setQuantity(item.getQuantity());
        entity.setPurchaseCost(item.getPurchaseCost());
        entity.setMrp(item.getMrp());
        entity.setLineTotal(item.getLineTotal());

        return entity;
    }
}