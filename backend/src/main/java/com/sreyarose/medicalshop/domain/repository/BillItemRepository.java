
package com.sreyarose.medicalshop.domain.repository;

import com.sreyarose.medicalshop.domain.entity.BillItem;

import java.util.List;

public interface BillItemRepository {

    BillItem save(BillItem billItem);

    List<BillItem> saveAll(List<BillItem> billItems);

    List<BillItem> findByBillId(Long billId);
}
