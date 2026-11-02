
package com.sreyarose.medicalshop.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillItemJpaRepository
        extends JpaRepository<BillItemJpaEntity, Long> {

    List<BillItemJpaEntity> findByBillId(Long billId);
}
