
package com.sreyarose.medicalshop.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillJpaRepository
        extends JpaRepository<BillJpaEntity, Long> {

    Optional<BillJpaEntity> findByBillNumber(String billNumber);

    boolean existsByBillNumber(String billNumber);
}
    