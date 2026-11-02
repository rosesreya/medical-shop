package com.sreyarose.medicalshop.infrastructure.mapper;

import com.sreyarose.medicalshop.domain.entity.Supplier;
import com.sreyarose.medicalshop.infrastructure.persistence.SupplierJpaEntity;

public class SupplierMapper {

    public static Supplier toDomain(SupplierJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Supplier(
                entity.getId(),
                entity.getName(),
                entity.getContactPerson(),
                entity.getContactNumber(),
                entity.getEmail(),
                entity.getAddress(),
                entity.isActive()
        );
    }

    public static SupplierJpaEntity toJpaEntity(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        SupplierJpaEntity entity = new SupplierJpaEntity();

        entity.setId(supplier.getId());
        entity.setName(supplier.getName());
        entity.setContactPerson(supplier.getContactPerson());
        entity.setContactNumber(supplier.getContactNumber());
        entity.setEmail(supplier.getEmail());
        entity.setAddress(supplier.getAddress());
        entity.setActive(supplier.isActive());

        return entity;
    }
}