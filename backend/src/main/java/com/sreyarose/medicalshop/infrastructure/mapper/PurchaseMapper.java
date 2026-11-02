package com.sreyarose.medicalshop.infrastructure.mapper;

import com.sreyarose.medicalshop.domain.entity.Purchase;
import com.sreyarose.medicalshop.infrastructure.persistence.PurchaseJpaEntity;

public class PurchaseMapper {

    public static Purchase toDomain(PurchaseJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Purchase(
                entity.getId(),
                entity.getSupplierId(),
                entity.getInvoiceNumber(),
                entity.getPurchaseDate(),
                entity.getTotalAmount()
        );
    }

    public static PurchaseJpaEntity toJpaEntity(Purchase purchase) {
        if (purchase == null) {
            return null;
        }

        PurchaseJpaEntity entity = new PurchaseJpaEntity();

        entity.setId(purchase.getId());
        entity.setSupplierId(purchase.getSupplierId());
        entity.setInvoiceNumber(purchase.getInvoiceNumber());
        entity.setPurchaseDate(purchase.getPurchaseDate());
        entity.setTotalAmount(purchase.getTotalAmount());

        return entity;
    }
}