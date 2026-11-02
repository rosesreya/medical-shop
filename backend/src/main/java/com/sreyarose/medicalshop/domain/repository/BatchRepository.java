package com.sreyarose.medicalshop.domain.repository;

import com.sreyarose.medicalshop.domain.entity.Batch;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BatchRepository {

    Batch save(Batch batch);

    Optional<Batch> findById(Long id);

    Optional<Batch> findByProductIdAndBatchNumberForUpdate(
            Long productId,
            String batchNumber
    );

    List<Batch> findByProductId(Long productId);

    List<Batch> findAll();
    List<Batch> findEligibleByProductIdForUpdate(
        Long productId,
        LocalDate today
);
}