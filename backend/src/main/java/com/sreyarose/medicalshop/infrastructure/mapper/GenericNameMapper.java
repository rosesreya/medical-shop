
package com.sreyarose.medicalshop.infrastructure.mapper;

import com.sreyarose.medicalshop.domain.entity.GenericName;
import com.sreyarose.medicalshop.infrastructure.persistence.GenericNameJpaEntity;

public class GenericNameMapper {

    private GenericNameMapper() {
    }

    public static GenericName toDomain(GenericNameJpaEntity entity) {
        return new GenericName(
                entity.getId(),
                entity.getName(),
                entity.isActive()
        );
    }

    public static GenericNameJpaEntity toJpaEntity(GenericName genericName) {
        return new GenericNameJpaEntity(
                genericName.getName(),
                genericName.isActive()
        );
    }
}
