
package com.sreyarose.medicalshop.domain.repository;

import com.sreyarose.medicalshop.domain.entity.GenericName;

import java.util.List;
import java.util.Optional;

public interface GenericNameRepository {
    GenericName save(GenericName genericName);
    Optional<GenericName> findById(Long id);
    List<GenericName> findAll();
    boolean existsByNameIgnoreCase(String name);
}