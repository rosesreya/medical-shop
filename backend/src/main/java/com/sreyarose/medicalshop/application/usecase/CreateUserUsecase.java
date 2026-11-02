
package com.sreyarose.medicalshop.application.usecase;

import com.sreyarose.medicalshop.domain.entity.User;
import com.sreyarose.medicalshop.domain.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateUserUsecase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUsecase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User execute(
            String fullName,
            String username,
            String rawPassword,
            String role) {

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException(
                    "Full name cannot be empty");
        }

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty");
        }

        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters");
        }

        if (role == null ||
                (!role.equals("ADMIN") && !role.equals("STAFF"))) {
            throw new IllegalArgumentException(
                    "Role must be ADMIN or STAFF");
        }

        String normalizedUsername = username.trim().toLowerCase();

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException(
                    "Username already exists");
        }

        String passwordHash = passwordEncoder.encode(rawPassword);

        User user = new User(
                fullName,
                normalizedUsername,
                passwordHash,
                role
        );

        return userRepository.save(user);
    }
}
