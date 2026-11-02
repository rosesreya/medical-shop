
package com.sreyarose.medicalshop.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "generic_names")
public class GenericNameJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean active = true;

    protected GenericNameJpaEntity() {
        // Required by JPA
    }

    public GenericNameJpaEntity(String name, boolean active) {
        this.name = name;
        this.active = active;
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
    public void setName(String name) {
    this.name = name;
}

public void setActive(boolean active) {
    this.active = active;
}
}
