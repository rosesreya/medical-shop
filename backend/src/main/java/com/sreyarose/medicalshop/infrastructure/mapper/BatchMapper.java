package com.sreyarose.medicalshop.infrastructure.mapper;

import com.sreyarose.medicalshop.domain.entity.Batch;
import com.sreyarose.medicalshop.infrastructure.persistence.BatchJpaEntity;

public class BatchMapper {

    public static Batch toDomain(BatchJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Batch(
                entity.getId(),
                entity.getProductId(),
                entity.getBatchNumber(),
                entity.getManufacturingDate(),
                entity.getExpiryDate(),
                entity.getQuantity(),
                entity.getMrp()
        );
    }

    public static BatchJpaEntity toJpaEntity(Batch batch) {
        if (batch == null) {
            return null;
        }

        BatchJpaEntity entity = new BatchJpaEntity();

        entity.setId(batch.getId());
        entity.setProductId(batch.getProductId());
        entity.setBatchNumber(batch.getBatchNumber());
        entity.setManufacturingDate(batch.getManufacturingDate());
        entity.setExpiryDate(batch.getExpiryDate());
        entity.setQuantity(batch.getQuantity());
        entity.setMrp(batch.getMrp());

        return entity;
    }
}