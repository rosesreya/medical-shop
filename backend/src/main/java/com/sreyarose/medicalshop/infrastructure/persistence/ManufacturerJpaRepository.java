
package com.sreyarose.medicalshop.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ManufacturerJpaRepository
        extends JpaRepository<ManufacturerJpaEntity, Long> {

    boolean existsByNameIgnoreCase(String name);
}
