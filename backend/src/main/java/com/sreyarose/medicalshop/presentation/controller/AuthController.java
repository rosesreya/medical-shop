
package com.sreyarose.medicalshop.presentation.controller;

import com.sreyarose.medicalshop.application.usecase.LoginUsecase;
import com.sreyarose.medicalshop.application.usecase.LoginUsecase.LoginResult;
import com.sreyarose.medicalshop.presentation.dto.LoginRequest;
import com.sreyarose.medicalshop.presentation.dto.LoginResponse;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginUsecase loginUsecase;

    public AuthController(LoginUsecase loginUsecase) {
        this.loginUsecase = loginUsecase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResult result = loginUsecase.execute(
                request.getUsername(),
                request.getPassword()
        );

        LoginResponse response = new LoginResponse(
                result.getToken(),
                result.getUserId(),
                result.getFullName(),
                result.getUsername(),
                result.getRole()
        );

        return ResponseEntity.ok(response);
    }
}
