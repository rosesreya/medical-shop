
package com.sreyarose.medicalshop.domain.entity;

public class Manufacturer {

    private Long id;
    private String name;
    private boolean active;
    private String address;
    private String contactNumber;
    private String email;

    public Manufacturer(
            Long id,
            String name,
            boolean active,
            String address,
            String contactNumber,
            String email) {

        this.id = id;
        setName(name);
        this.active = active;
        setAddress(address);
        setContactNumber(contactNumber);
        setEmail(email);
    }

    public Manufacturer(String name) {
        setName(name);
        this.active = true;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Manufacturer name cannot be empty");
        }

        this.name = name.trim();
    }

    public void setAddress(String address) {
        this.address = normalizeOptionalField(address);
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = normalizeOptionalField(contactNumber);
    }

    public void setEmail(String email) {
        this.email = normalizeOptionalField(email);
    }

    private String normalizeOptionalField(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
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

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
