package com.example.socio.controller;

import com.example.socio.model.ApiResponse;
import com.example.socio.model.LoginRequest;
import com.example.socio.model.UserRegistrationRequest;
import com.example.socio.model.UserResponse;
import com.example.socio.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", token));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<com.example.socio.model.UserResponse>> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse user = authService.register(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "User registered successfully", user), HttpStatus.CREATED);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody String email) {
        authService.resetPassword(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password reset email sent", null));
    }
}