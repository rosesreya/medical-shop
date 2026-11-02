
package com.sreyarose.medicalshop.presentation.dto;

public class CategoryResponse {

    private String name;
    private Long id;
    private boolean active;

    public CategoryResponse(Long id, String name, boolean active) {
        this.id = id;
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
}
