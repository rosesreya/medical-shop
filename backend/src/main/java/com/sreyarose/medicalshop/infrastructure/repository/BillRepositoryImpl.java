
package com.sreyarose.medicalshop.infrastructure.repository;

import com.sreyarose.medicalshop.domain.entity.Bill;
import com.sreyarose.medicalshop.domain.repository.BillRepository;
import com.sreyarose.medicalshop.infrastructure.mapper.BillMapper;
import com.sreyarose.medicalshop.infrastructure.persistence.BillJpaEntity;
import com.sreyarose.medicalshop.infrastructure.persistence.BillJpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BillRepositoryImpl implements BillRepository {

    private final BillJpaRepository billJpaRepository;

    public BillRepositoryImpl(BillJpaRepository billJpaRepository) {
        this.billJpaRepository = billJpaRepository;
    }

    @Override
    public Bill save(Bill bill) {
        BillJpaEntity entity = BillMapper.toJpaEntity(bill);
        BillJpaEntity saved = billJpaRepository.save(entity);

        return BillMapper.toDomain(saved);
    }

    @Override
    public Optional<Bill> findById(Long id) {
        return billJpaRepository.findById(id)
                .map(BillMapper::toDomain);
    }

    @Override
    public List<Bill> findAll() {
        return billJpaRepository.findAll()
                .stream()
                .map(BillMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByBillNumber(String billNumber) {
        return billJpaRepository.existsByBillNumber(billNumber);
    }
}
