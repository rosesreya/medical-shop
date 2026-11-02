package com.sreyarose.medicalshop.domain.entity;

public class Supplier {

    private Long id;
    private String name;
    private String contactPerson;
    private String contactNumber;
    private String email;
    private String address;
    private boolean active;

    public Supplier(
            Long id,
            String name,
            String contactPerson,
            String contactNumber,
            String email,
            String address,
            boolean active) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Supplier name is required");
        }

        if (contactNumber == null || contactNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Supplier contact number is required");
        }

        this.id = id;
        this.name = name.trim();
        this.contactPerson = contactPerson;
        this.contactNumber = contactNumber.trim();
        this.email = email;
        this.address = address;
        this.active = active;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
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