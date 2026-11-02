
package com.sreyarose.medicalshop.infrastructure.config;

import com.sreyarose.medicalshop.application.usecase.CreateUserUsecase;
import com.sreyarose.medicalshop.domain.entity.User;
import com.sreyarose.medicalshop.domain.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserRepository userRepository;
    private final CreateUserUsecase createUserUsecase;

    @Value("${app.bootstrap-admin.enabled:false}")
    private boolean enabled;

    @Value("${app.bootstrap-admin.full-name:}")
    private String fullName;

    @Value("${app.bootstrap-admin.username:}")
    private String username;

    @Value("${app.bootstrap-admin.password:}")
    private String password;

    public AdminBootstrap(
            UserRepository userRepository,
            CreateUserUsecase createUserUsecase) {
        this.userRepository = userRepository;
        this.createUserUsecase = createUserUsecase;
    }

    @Override
    public void run(ApplicationArguments args) {

        if (!enabled) {
            return;
        }

        boolean adminExists = userRepository.findAll()
                .stream()
                .anyMatch(user -> "ADMIN".equals(user.getRole()));

        if (adminExists) {
            logger.info(
                    "Admin bootstrap skipped; an ADMIN account already exists.");
            return;
        }

        if (fullName == null || fullName.isBlank()
                || username == null || username.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "Admin bootstrap is enabled, but required credentials are missing.");
        }

        String normalizedUsername =
                username.trim().toLowerCase(java.util.Locale.ROOT);

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new IllegalStateException(
                    "The bootstrap username already exists, but no ADMIN account exists. "
                    + "Configure a different username.");
        }

        User admin = createUserUsecase.execute(
                fullName,
                normalizedUsername,
                password,
                "ADMIN");

        logger.info(
                "Initial ADMIN account created with username '{}'.",
                admin.getUsername());
    }
}
