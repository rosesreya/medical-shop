
package com.sreyarose.medicalshop.infrastructure.mapper;

import com.sreyarose.medicalshop.domain.entity.BillItem;
import com.sreyarose.medicalshop.infrastructure.persistence.BillItemJpaEntity;

import java.math.BigDecimal;

public final class BillItemMapper {

    private BillItemMapper() {
    }

    public static BillItemJpaEntity toJpaEntity(BillItem billItem) {
        BillItemJpaEntity entity = new BillItemJpaEntity();

        entity.setBillId(billItem.getBillId());
        entity.setProductId(billItem.getProductId());
        entity.setBatchId(billItem.getBatchId());
        entity.setQuantity(billItem.getQuantity());
        entity.setSellingRate(billItem.getSellingRate());
        entity.setDiscountAmount(billItem.getDiscountAmount());

        entity.setTaxableAmount(billItem.getTaxableAmount());
        entity.setCgstRate(billItem.getCgstRate());
        entity.setCgstAmount(billItem.getCgstAmount());
        entity.setSgstRate(billItem.getSgstRate());
        entity.setSgstAmount(billItem.getSgstAmount());

        entity.setLineTotal(billItem.getLineTotal());

        return entity;
    }

    public static BillItem toDomain(BillItemJpaEntity entity) {
        return new BillItem(
                entity.getId(),
                entity.getBillId(),
                entity.getProductId(),
                entity.getBatchId(),
                entity.getQuantity(),
                entity.getSellingRate(),
                entity.getDiscountAmount(),
                defaultIfNull(entity.getCgstRate()),
                defaultIfNull(entity.getSgstRate())
        );
    }

    private static BigDecimal defaultIfNull(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
