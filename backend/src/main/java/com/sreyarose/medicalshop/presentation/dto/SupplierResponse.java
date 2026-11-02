package com.sreyarose.medicalshop.presentation.dto;

import com.sreyarose.medicalshop.domain.entity.Supplier;

public class SupplierResponse {

    private Long id;
    private String name;
    private String contactPerson;
    private String contactNumber;
    private String email;
    private String address;
    private boolean active;

    public SupplierResponse(
            Long id,
            String name,
            String contactPerson,
            String contactNumber,
            String email,
            String address,
            boolean active) {

        this.id = id;
        this.name = name;
        this.contactPerson = contactPerson;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.active = active;
    }

    public static SupplierResponse from(Supplier supplier) {
        return new SupplierResponse(
                supplier.getId(),
                supplier.getName(),
                supplier.getContactPerson(),
                supplier.getContactNumber(),
                supplier.getEmail(),
                supplier.getAddress(),
                supplier.isActive()
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public boolean isActive() {
        return active;
    }
}