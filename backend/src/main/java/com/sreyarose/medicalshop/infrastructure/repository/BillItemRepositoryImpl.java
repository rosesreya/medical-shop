
package com.sreyarose.medicalshop.infrastructure.repository;

import com.sreyarose.medicalshop.domain.entity.BillItem;
import com.sreyarose.medicalshop.domain.repository.BillItemRepository;
import com.sreyarose.medicalshop.infrastructure.mapper.BillItemMapper;
import com.sreyarose.medicalshop.infrastructure.persistence.BillItemJpaEntity;
import com.sreyarose.medicalshop.infrastructure.persistence.BillItemJpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BillItemRepositoryImpl implements BillItemRepository {

    private final BillItemJpaRepository billItemJpaRepository;

    public BillItemRepositoryImpl(
            BillItemJpaRepository billItemJpaRepository
    ) {
        this.billItemJpaRepository = billItemJpaRepository;
    }

    @Override
    public BillItem save(BillItem billItem) {
        BillItemJpaEntity entity =
                BillItemMapper.toJpaEntity(billItem);

        BillItemJpaEntity saved =
                billItemJpaRepository.save(entity);

        return BillItemMapper.toDomain(saved);
    }

    @Override
    public List<BillItem> saveAll(List<BillItem> billItems) {
        List<BillItemJpaEntity> entities = billItems.stream()
                .map(BillItemMapper::toJpaEntity)
                .toList();

        return billItemJpaRepository.saveAll(entities)
                .stream()
                .map(BillItemMapper::toDomain)
                .toList();
    }

    @Override
    public List<BillItem> findByBillId(Long billId) {
        return billItemJpaRepository.findByBillId(billId)
                .stream()
                .map(BillItemMapper::toDomain)
                .toList();
    }
}
