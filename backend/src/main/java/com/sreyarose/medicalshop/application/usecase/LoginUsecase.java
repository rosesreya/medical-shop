
package com.sreyarose.medicalshop.application.usecase;

import com.sreyarose.medicalshop.domain.entity.User;
import com.sreyarose.medicalshop.domain.repository.UserRepository;
import com.sreyarose.medicalshop.infrastructure.config.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUsecase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginUsecase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResult execute(String username, String rawPassword) {

        if (username == null || username.isBlank()
                || rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException(
                    "Username and password are required");
        }

        User user = userRepository
                .findByUsername(username.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid username or password"));

        if (!user.isActive()
                || !passwordEncoder.matches(
                        rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException(
                    "Invalid username or password");
        }

        String token = jwtService.generateToken(user);

        return new LoginResult(
                token,
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getRole()
        );
    }

    public static class LoginResult {

        private final String token;
        private final Long userId;
        private final String fullName;
        private final String username;
        private final String role;

        public LoginResult(
                String token,
                Long userId,
                String fullName,
                String username,
                String role) {
            this.token = token;
            this.userId = userId;
            this.fullName = fullName;
            this.username = username;
            this.role = role;
        }

        public String getToken() {
            return token;
        }

        public Long getUserId() {
            return userId;
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
    }
}
