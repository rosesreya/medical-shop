
package com.sreyarose.medicalshop.presentation.dto;

public class GenericNameResponse {

    private String name;
    private Long id;
    private boolean active;

    public GenericNameResponse(Long id, String name, boolean active) {
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
