
package com.sreyarose.medicalshop.presentation.dto;

public class ManufacturerResponse {

    private Long id;
    private String name;
    private boolean active;
    private String address;
    private String contactNumber;
    private String email;

    public ManufacturerResponse(
            Long id,
            String name,
            boolean active,
            String address,
            String contactNumber,
            String email) {

        this.id = id;
        this.name = name;
        this.active = active;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public String getAddress() {
        return address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }
}