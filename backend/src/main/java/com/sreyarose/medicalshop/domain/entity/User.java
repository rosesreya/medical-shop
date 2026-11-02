
package com.sreyarose.medicalshop.domain.entity;

public class User {

    private Long id;
    private String fullName;
    private String username;
    private String passwordHash;
    private String role;
    private boolean active;

    public User(
            Long id,
            String fullName,
            String username,
            String passwordHash,
            String role,
            boolean active) {

        this.id = id;
        setFullName(fullName);
        setUsername(username);
        setPasswordHash(passwordHash);
        setRole(role);
        this.active = active;
    }

    public User(
            String fullName,
            String username,
            String passwordHash,
            String role) {

        this(null, fullName, username, passwordHash, role, true);
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException(
                    "Full name cannot be empty");
        }

        this.fullName = fullName.trim();
    }

    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty");
        }

        this.username = username.trim().toLowerCase();
    }

    public void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException(
                    "Password hash cannot be empty");
        }

        this.passwordHash = passwordHash;
    }

    public void setRole(String role) {
        if (role == null ||
                (!role.equals("ADMIN") && !role.equals("STAFF"))) {
            throw new IllegalArgumentException(
                    "Role must be ADMIN or STAFF");
        }

        this.role = role;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
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
