
package com.sreyarose.medicalshop.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "manufacturers")
public class ManufacturerJpaEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean active = true;

    private String address;
    private String contactNumber;
    private String email;

    protected ManufacturerJpaEntity() {
    }

    public ManufacturerJpaEntity(
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
