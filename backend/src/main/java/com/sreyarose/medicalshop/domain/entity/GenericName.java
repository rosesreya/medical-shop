
package com.sreyarose.medicalshop.domain.entity;

public class GenericName {

    private Long id;
    private String name;
    private boolean active;

    public GenericName(Long id, String name, boolean active) {
        this.id = id;
        setName(name);
        this.active = active;
    }

    public GenericName(String name) {
        setName(name);
        this.active = true;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Generic name cannot be empty");
        }

        this.name = name.trim();
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

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
