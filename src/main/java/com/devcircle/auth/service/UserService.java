package com.devcircle.auth.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devcircle.auth.dto.LoginRequest;
import com.devcircle.auth.dto.LoginResponse;
import com.devcircle.auth.dto.RegisterRequest;
import com.devcircle.auth.dto.RegisterResponse;
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

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already in use.");
        }
        String hashedPassword = encoder.encode(request.getPassword());
        User user = User.build(request.getFullName(), request.getEmail(), hashedPassword, null);

        User savedUser = userRepository.save(user);
        String jwt = issueJwt(user.getId(), user.getEmail(), Collections.emptyList());
        return new RegisterResponse(savedUser.getId(), jwt,
                "Successfully created the user");
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email id not found."));
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Incorrect password.");
        }
        String jwt = issueJwt(user.getId(), user.getEmail(), Collections.emptyList());
        return new LoginResponse(user.getId(), jwt);
    }

    private String issueJwt(UUID userId, String email, List<String> roles) {
        return jwtService.generateToken(userId, email, roles);
    }

}
