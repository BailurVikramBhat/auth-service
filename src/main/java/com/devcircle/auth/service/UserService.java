package com.devcircle.auth.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devcircle.auth.dto.LoginRequest;
import com.devcircle.auth.dto.RegisterRequest;
import com.devcircle.auth.exception.DuplicateResourceException;
import com.devcircle.auth.exception.InvalidCredentialsException;
import com.devcircle.auth.model.User;
import com.devcircle.auth.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder encoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public UUID register(RegisterRequest request) {
        // TODO: Check email, username already exists

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already in use.");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username is already in use.");
        }

        String hashedPassword = encoder.encode(request.getPassword());
        User user = User.build(request.getUsername(), request.getEmail(), hashedPassword, null);

        return userRepository.save(user).getId();
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email id not found."));
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Incorrect password.");
        }
        return jwtService.generateToken(user.getEmail());
    }

}
