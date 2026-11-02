
package com.sreyarose.medicalshop.domain.repository;

import java.util.List;
import java.util.Optional;

import com.sreyarose.medicalshop.domain.entity.Manufacturer;

public interface ManufacturerRepository {
    Manufacturer save(Manufacturer manufacturer);
    Optional<Manufacturer> findById(Long id);
    List<Manufacturer> findAll();
    boolean existsByNameIgnoreCase(String name);
}
