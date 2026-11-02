
package com.sreyarose.medicalshop.infrastructure.repository;

import com.sreyarose.medicalshop.domain.entity.GenericName;
import com.sreyarose.medicalshop.domain.repository.GenericNameRepository;
import com.sreyarose.medicalshop.infrastructure.mapper.GenericNameMapper;
import com.sreyarose.medicalshop.infrastructure.persistence.GenericNameJpaEntity;
import com.sreyarose.medicalshop.infrastructure.persistence.GenericNameJpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GenericNameRepositoryImpl implements GenericNameRepository {

    private final GenericNameJpaRepository jpaRepository;

    public GenericNameRepositoryImpl(
            GenericNameJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public GenericName save(GenericName genericName) {
        GenericNameJpaEntity entity;

        if (genericName.getId() == null) {
            entity = GenericNameMapper.toJpaEntity(genericName);
        } else {
            entity = jpaRepository.findById(genericName.getId())
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Generic name not found during update"));

            entity.setName(genericName.getName());
            entity.setActive(genericName.isActive());
        }

        GenericNameJpaEntity savedEntity = jpaRepository.save(entity);

        return GenericNameMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<GenericName> findById(Long id) {
        return jpaRepository.findById(id)
                .map(GenericNameMapper::toDomain);
    }

    @Override
    public List<GenericName> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(GenericNameMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return jpaRepository.existsByNameIgnoreCase(name);
    }
}
