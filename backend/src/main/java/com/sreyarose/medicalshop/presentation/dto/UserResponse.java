
package com.sreyarose.medicalshop.presentation.dto;

public class UserResponse {

    private Long id;
    private String fullName;
    private String username;
    private String role;
    private boolean active;

    public UserResponse(
            Long id,
            String fullName,
            String username,
            String role,
            boolean active) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.role = role;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }
}
