
package com.sreyarose.medicalshop.presentation.dto;

import com.sreyarose.medicalshop.domain.entity.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public class BillRequest {

    private String customerName;
    private String customerMobile;
    private PaymentMethod paymentMethod;
    private BigDecimal amountReceived;
    private List<BillItemRequest> items;

    public BillRequest() {
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmountReceived() {
        return amountReceived;
    }

    public void setAmountReceived(BigDecimal amountReceived) {
        this.amountReceived = amountReceived;
    }

    public List<BillItemRequest> getItems() {
        return items;
    }

    public void setItems(List<BillItemRequest> items) {
        this.items = items;
    }
}
