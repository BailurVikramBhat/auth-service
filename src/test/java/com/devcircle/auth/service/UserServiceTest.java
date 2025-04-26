package com.devcircle.auth.service;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import com.devcircle.auth.dto.LoginRequest;
import com.devcircle.auth.dto.RegisterRequest;
import com.devcircle.auth.exception.DuplicateResourceException;
import com.devcircle.auth.exception.InvalidCredentialsException;
import com.devcircle.auth.model.User;
import com.devcircle.auth.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void registerSuccess_returnsNewUuid() {
        var req = new RegisterRequest();
        req.setFullName("Vikram Bhat");
        req.setEmail("info@google.com");
        req.setPassword("Simple123@");
        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(req.getPassword())).thenReturn("mockEncodedPassword");

        var saved = User.build(req.getFullName(), req.getEmail(), "mockEncodedPassword", null);
        UUID fakeId = UUID.randomUUID();
        saved.setId(fakeId);
        when(userRepository.save(any())).thenReturn(saved);

        UUID result = userService.register(req);
        assertEquals(fakeId, result);

        // verify repo.save was called with a User whose fields match
        verify(userRepository).save(argThat(u -> u.getFullName().equals(req.getFullName()) &&
                u.getEmail().equals(req.getEmail()) &&
                u.getPassword().equals("mockEncodedPassword")));
    }

    @Test
    void register_whenEmailExistsThrowDuplicateResourceException() {
        var req = new RegisterRequest();
        req.setFullName("Vikram Bhat");
        req.setEmail("info@google.com");
        req.setPassword("Simple123@");
        when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            userService.register(req);
        });
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_whenEmailNotFound_throwsInvalidCredentials() {
        var req = new LoginRequest();
        req.setEmail("nouser@dc.com");
        req.setPassword("pw");
        when(userRepository.findByEmail(req.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> userService.login(req));
    }

    @Test
    void login_whenPasswordWrong_throwsInvalidCredentials() {
        var req = new LoginRequest();
        req.setEmail("u@dc.com");
        req.setPassword("wrongpw");
        User u = User.build("U", req.getEmail(), "storedHash", null);
        when(userRepository.findByEmail(req.getEmail()))
                .thenReturn(Optional.of(u));
        when(passwordEncoder.matches("wrongpw", "storedHash")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.login(req));
    }

    @Test
    void login_whenCredentialsValid_returnsJwt() {
        var req = new LoginRequest();
        req.setEmail("vikram@dc.com");
        req.setPassword("rightpw");
        User u = User.build("Vikram", "vikram@dc.com", "hash123", null);
        when(userRepository.findByEmail("vikram@dc.com"))
                .thenReturn(Optional.of(u));
        when(passwordEncoder.matches("rightpw", "hash123")).thenReturn(true);
        when(jwtService.generateToken("vikram@dc.com")).thenReturn("tokenXYZ");

        String token = userService.login(req);
        assertEquals("tokenXYZ", token);
    }

}
