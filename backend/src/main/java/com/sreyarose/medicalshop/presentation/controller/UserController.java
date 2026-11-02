
package com.sreyarose.medicalshop.presentation.controller;

import com.sreyarose.medicalshop.application.usecase.CreateUserUsecase;
import com.sreyarose.medicalshop.domain.entity.User;
import com.sreyarose.medicalshop.domain.repository.UserRepository;
import com.sreyarose.medicalshop.presentation.dto.CreateUserRequest;
import com.sreyarose.medicalshop.presentation.dto.UserResponse;
import com.sreyarose.medicalshop.application.usecase.UpdateUserStatusUse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CreateUserUsecase createUserUsecase;
    private final UserRepository userRepository;
    private final UpdateUserStatusUse updateUserStatusUsecase;

    public UserController(
            CreateUserUsecase createUserUsecase,
            UserRepository userRepository,
            UpdateUserStatusUse updateUserStatusUsecase) {
        this.createUserUsecase = createUserUsecase;
        this.userRepository = userRepository;
        this.updateUserStatusUsecase = updateUserStatusUsecase;
    }

    // Create a Staff account
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody CreateUserRequest request) {

        User user = createUserUsecase.execute(
                request.getFullName(),
                request.getUsername(),
                request.getPassword(),
                "STAFF"
        );

        UserResponse response = new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getRole(),
                user.isActive()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Retrieve all users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getUsername(),
                        user.getRole(),
                        user.isActive()
                ))
                .toList();

        return ResponseEntity.ok(users);
    }
    
@PatchMapping("/{id}/deactivate")
public ResponseEntity<UserResponse> deactivateUser(
        @PathVariable Long id) {

    return updateUserStatus(id, false);
}

@PatchMapping("/{id}/activate")
public ResponseEntity<UserResponse> activateUser(
        @PathVariable Long id) {

    return updateUserStatus(id, true);
}

private ResponseEntity<UserResponse> updateUserStatus(
        Long id, boolean active) {

    User user = updateUserStatusUsecase.execute(id, active);

    UserResponse response = new UserResponse(
            user.getId(),
            user.getFullName(),
            user.getUsername(),
            user.getRole(),
            user.isActive()
    );

    return ResponseEntity.ok(response);
}

}
