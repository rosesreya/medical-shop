package com.sreyarose.medicalshop.domain.repository;

import com.sreyarose.medicalshop.domain.entity.PurchaseItem;

import java.util.List;

public interface PurchaseItemRepository {

    PurchaseItem save(PurchaseItem purchaseItem);

    List<PurchaseItem> saveAll(
            List<PurchaseItem> purchaseItems
    );

    List<PurchaseItem> findByPurchaseId(Long purchaseId);
}