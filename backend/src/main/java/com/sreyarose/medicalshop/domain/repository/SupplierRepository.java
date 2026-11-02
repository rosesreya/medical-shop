package com.sreyarose.medicalshop.domain.repository;

import com.sreyarose.medicalshop.domain.entity.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository {

    Supplier save(Supplier supplier);

    Optional<Supplier> findById(Long id);

    Optional<Supplier> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Supplier> findAll();

    List<Supplier> findAllActive();
}