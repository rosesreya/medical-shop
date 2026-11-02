
package com.sreyarose.medicalshop.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sreyarose.medicalshop.domain.entity.Manufacturer;
import com.sreyarose.medicalshop.domain.repository.ManufacturerRepository;
import com.sreyarose.medicalshop.infrastructure.mapper.ManufacturerMapper;
import com.sreyarose.medicalshop.infrastructure.persistence.ManufacturerJpaEntity;
import com.sreyarose.medicalshop.infrastructure.persistence.ManufacturerJpaRepository;

@Repository
public class ManufacturerRepositoryImpl implements ManufacturerRepository {

    private final ManufacturerJpaRepository jpaRepository;

    public ManufacturerRepositoryImpl(ManufacturerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Manufacturer save(Manufacturer manufacturer) {
        ManufacturerJpaEntity jpaEntity = ManufacturerMapper.toJpaManufacturer(manufacturer);
        ManufacturerJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return ManufacturerMapper.toDomainManufacturer(savedEntity);
    }

    @Override
    public Optional<Manufacturer> findById(Long id) {
        return jpaRepository.findById(id).map(ManufacturerMapper::toDomainManufacturer);
    }

    @Override
    public List<Manufacturer> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(ManufacturerMapper::toDomainManufacturer)
                .toList();
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return jpaRepository.existsByNameIgnoreCase(name);
    }
}
