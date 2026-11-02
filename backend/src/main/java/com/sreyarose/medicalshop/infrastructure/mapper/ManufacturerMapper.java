
package com.sreyarose.medicalshop.infrastructure.mapper;

import com.sreyarose.medicalshop.domain.entity.Manufacturer;
import com.sreyarose.medicalshop.infrastructure.persistence.ManufacturerJpaEntity;

public class ManufacturerMapper {

    // Domain to JPA entity
    public static ManufacturerJpaEntity toJpaManufacturer(
            Manufacturer manufacturer) {

        return new ManufacturerJpaEntity(
                manufacturer.getId(),
                manufacturer.getName(),
                manufacturer.isActive(),
                manufacturer.getAddress(),
                manufacturer.getContactNumber(),
                manufacturer.getEmail()
        );
    }

    // JPA entity to domain
    public static Manufacturer toDomainManufacturer(
            ManufacturerJpaEntity jpaEntity) {

        return new Manufacturer(
                jpaEntity.getId(),
                jpaEntity.getName(),
                jpaEntity.isActive(),
                jpaEntity.getAddress(),
                jpaEntity.getContactNumber(),
                jpaEntity.getEmail()
        );
    }
}
