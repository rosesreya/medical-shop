package com.sreyarose.medicalshop.infrastructure.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "suppliers")
public class SupplierJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "contact_person", length = 150)
    private String contactPerson;

    @Column(name = "contact_number", nullable = false, length = 20)
    private String contactNumber;

    @Column(length = 150)
    private String email;

    @Column(length = 500)
    private String address;

    @Column(nullable = false)
    private boolean active;

    public SupplierJpaEntity() {
        // Required by JPA
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}