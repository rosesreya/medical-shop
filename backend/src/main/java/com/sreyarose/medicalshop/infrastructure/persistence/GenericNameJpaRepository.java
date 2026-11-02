
package com.sreyarose.medicalshop.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GenericNameJpaRepository
        extends JpaRepository<GenericNameJpaEntity, Long> {

    boolean existsByNameIgnoreCase(String name);
}
