
package com.sreyarose.medicalshop.domain.repository;

import com.sreyarose.medicalshop.domain.entity.Bill;

import java.util.List;
import java.util.Optional;

public interface BillRepository {

    Bill save(Bill bill);

    Optional<Bill> findById(Long id);

    List<Bill> findAll();

    boolean existsByBillNumber(String billNumber);
}
