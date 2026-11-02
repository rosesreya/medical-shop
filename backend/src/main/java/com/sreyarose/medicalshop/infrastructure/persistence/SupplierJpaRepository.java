package com.sreyarose.medicalshop.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplierJpaRepository
        extends JpaRepository<SupplierJpaEntity, Long> {

    Optional<SupplierJpaEntity> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<SupplierJpaEntity> findByActiveTrue();

}