package com.devcircle.auth.controller;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devcircle.auth.dto.LoginRequest;
import com.devcircle.auth.dto.LoginResponse;
import com.devcircle.auth.dto.RegisterRequest;
import com.devcircle.auth.dto.RegisterResponse;
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
        RegisterResponse response = userService.register(request);
        URI location = URI.create("/users/" + response.userId());
        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
