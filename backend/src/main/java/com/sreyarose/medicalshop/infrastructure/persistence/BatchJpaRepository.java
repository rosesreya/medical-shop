package com.sreyarose.medicalshop.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BatchJpaRepository
        extends JpaRepository<BatchJpaEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("""
    SELECT b FROM BatchJpaEntity b
    WHERE b.productId = :productId
      AND b.batchNumber = :batchNumber
    """)
Optional<BatchJpaEntity> findByProductIdAndBatchNumberForUpdate(
        @Param("productId") Long productId,
        @Param("batchNumber") String batchNumber
);
    List<BatchJpaEntity> findByProductId(Long productId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("""
    SELECT b FROM BatchJpaEntity b
    WHERE b.productId = :productId
      AND b.expiryDate >= :today
      AND b.quantity > 0
    ORDER BY b.expiryDate ASC, b.id ASC
    """)
List<BatchJpaEntity> findEligibleByProductIdForUpdate(
        @Param("productId") Long productId,
        @Param("today") LocalDate today
);
}