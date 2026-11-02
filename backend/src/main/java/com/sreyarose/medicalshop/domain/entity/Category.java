package com.sreyarose.medicalshop.domain.entity;

public class Category {

    private Long id;
    private String name;
    private boolean active;

    public Category(Long id, String name, boolean active) {
        this.id = id;
        setName(name);
        this.active = active;
    }

    public Category(String name) {
        setName(name);
        active = true;
    }

    public void setName(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                "Category name cannot be empty"
            );
        }

        name = name.trim();

        if (!name.matches("^(?=.*[A-Za-z])[A-Za-z0-9 _:&/()\\-]+$")) {
            throw new IllegalArgumentException(
                "Invalid category name. Allowed characters are letters, digits, spaces, -, _, :, &, /, (, )."
            );
        }

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }
    public boolean isActive(){
        return active;
    }
    public void activate(){
        active = true;
    }
    public void deactivate(){
        active = false;
    }
}