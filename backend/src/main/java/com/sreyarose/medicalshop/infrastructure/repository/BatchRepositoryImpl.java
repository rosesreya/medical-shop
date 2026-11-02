package com.sreyarose.medicalshop.infrastructure.repository;

import com.sreyarose.medicalshop.domain.entity.Batch;
import com.sreyarose.medicalshop.domain.repository.BatchRepository;
import com.sreyarose.medicalshop.infrastructure.mapper.BatchMapper;
import com.sreyarose.medicalshop.infrastructure.persistence.BatchJpaEntity;
import com.sreyarose.medicalshop.infrastructure.persistence.BatchJpaRepository;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class BatchRepositoryImpl implements BatchRepository {

    private final BatchJpaRepository batchJpaRepository;

    public BatchRepositoryImpl(BatchJpaRepository batchJpaRepository) {
        this.batchJpaRepository = batchJpaRepository;
    }

    @Override
    public Batch save(Batch batch) {
        BatchJpaEntity entity = BatchMapper.toJpaEntity(batch);

        BatchJpaEntity savedEntity = batchJpaRepository.save(entity);

        return BatchMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Batch> findById(Long id) {
        return batchJpaRepository.findById(id)
                .map(BatchMapper::toDomain);
    }

    @Override
    public Optional<Batch> findByProductIdAndBatchNumberForUpdate(
            Long productId,
            String batchNumber) {

        return batchJpaRepository
                .findByProductIdAndBatchNumberForUpdate(
                        productId,
                        batchNumber.trim()
                )
                .map(BatchMapper::toDomain);
    }

    @Override
    public List<Batch> findByProductId(Long productId) {
        return batchJpaRepository.findByProductId(productId)
                .stream()
                .map(BatchMapper::toDomain)
                .toList();
    }

    @Override
    public List<Batch> findAll() {
        return batchJpaRepository.findAll()
                .stream()
                .map(BatchMapper::toDomain)
                .toList();
    }
    @Override
public List<Batch> findEligibleByProductIdForUpdate(
        Long productId,
        LocalDate today
) {
    return batchJpaRepository
            .findEligibleByProductIdForUpdate(productId, today)
            .stream()
            .map(BatchMapper::toDomain)
            .toList();
}
}