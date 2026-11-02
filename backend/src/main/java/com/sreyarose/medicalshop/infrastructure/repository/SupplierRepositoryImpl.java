package com.sreyarose.medicalshop.infrastructure.repository;

import com.sreyarose.medicalshop.domain.entity.Supplier;
import com.sreyarose.medicalshop.domain.repository.SupplierRepository;
import com.sreyarose.medicalshop.infrastructure.mapper.SupplierMapper;
import com.sreyarose.medicalshop.infrastructure.persistence.SupplierJpaEntity;
import com.sreyarose.medicalshop.infrastructure.persistence.SupplierJpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SupplierRepositoryImpl implements SupplierRepository {

    private final SupplierJpaRepository supplierJpaRepository;

    public SupplierRepositoryImpl(
            SupplierJpaRepository supplierJpaRepository) {
        this.supplierJpaRepository = supplierJpaRepository;
    }

    @Override
    public Supplier save(Supplier supplier) {
        SupplierJpaEntity entity =
                SupplierMapper.toJpaEntity(supplier);

        SupplierJpaEntity savedEntity =
                supplierJpaRepository.save(entity);

        return SupplierMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Supplier> findById(Long id) {
        return supplierJpaRepository.findById(id)
                .map(SupplierMapper::toDomain);
    }

    @Override
    public Optional<Supplier> findByNameIgnoreCase(String name) {
        return supplierJpaRepository
                .findByNameIgnoreCase(name.trim())
                .map(SupplierMapper::toDomain);
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return supplierJpaRepository
                .existsByNameIgnoreCase(name.trim());
    }

    @Override
    public List<Supplier> findAll() {
        return supplierJpaRepository.findAll()
                .stream()
                .map(SupplierMapper::toDomain)
                .toList();
    }

    @Override
    public List<Supplier> findAllActive() {
        return supplierJpaRepository.findByActiveTrue()
                .stream()
                .map(SupplierMapper::toDomain)
                .toList();
    }
}