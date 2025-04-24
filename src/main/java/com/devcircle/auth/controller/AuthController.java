package com.devcircle.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devcircle.auth.dto.LoginRequest;
import com.devcircle.auth.dto.RegisterRequest;
import com.devcircle.auth.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        var userId = userService.register(request);
        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully.",
                "userId", userId));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(Map.of("message", "Login successful", "token", token));
    }
}
