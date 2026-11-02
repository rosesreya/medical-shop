
package com.sreyarose.medicalshop.infrastructure.mapper;

import com.sreyarose.medicalshop.domain.entity.Bill;
import com.sreyarose.medicalshop.infrastructure.persistence.BillJpaEntity;

public final class BillMapper {

    private BillMapper() {
    }

    public static BillJpaEntity toJpaEntity(Bill bill) {
        BillJpaEntity entity = new BillJpaEntity();

        entity.setBillNumber(bill.getBillNumber());
        entity.setBillDate(bill.getBillDate());
        entity.setCustomerName(bill.getCustomerName());
        entity.setCustomerMobile(bill.getCustomerMobile());
        entity.setSubtotal(bill.getSubtotal());
        entity.setDiscountAmount(bill.getDiscountAmount());
        entity.setGrandTotal(bill.getGrandTotal());

        entity.setTotalTaxableAmount(bill.getTotalTaxableAmount());
        entity.setTotalCgstAmount(bill.getTotalCgstAmount());
        entity.setTotalSgstAmount(bill.getTotalSgstAmount());

        entity.setPaymentMethod(bill.getPaymentMethod());
        entity.setPaymentStatus(bill.getPaymentStatus());
        entity.setAmountReceived(bill.getAmountReceived());
        entity.setChangeAmount(bill.getChangeAmount());

        return entity;
    }

    public static Bill toDomain(BillJpaEntity entity) {
        return new Bill(
                entity.getId(),
                entity.getBillNumber(),
                entity.getBillDate(),
                entity.getCustomerName(),
                entity.getCustomerMobile(),
                entity.getSubtotal(),
                entity.getDiscountAmount(),
                entity.getGrandTotal(),
                entity.getTotalTaxableAmount(),
                entity.getTotalCgstAmount(),
                entity.getTotalSgstAmount(),
                entity.getPaymentMethod(),
                entity.getPaymentStatus(),
                entity.getAmountReceived(),
                entity.getChangeAmount()
        );
    }
}
