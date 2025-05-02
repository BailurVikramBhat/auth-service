package com.devcircle.auth.service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
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
import com.devcircle.auth.dto.LoginResponse;
import com.devcircle.auth.dto.RegisterRequest;
import com.devcircle.auth.dto.RegisterResponse;
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
    void registerSuccess_returnsRegisterResponse() {
        var req = new RegisterRequest();
        req.setFullName("Vikram Bhat");
        req.setEmail("info@google.com");
        req.setPassword("Simple123@");
        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(req.getPassword())).thenReturn("mockEncodedPassword");

        var saved = User.build(req.getFullName(), req.getEmail(), "mockEncodedPassword", null);
        UUID fakeId = UUID.randomUUID();
        saved.setId(fakeId);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0, User.class);
                    u.setId(fakeId);
                    return u;
                });
        when(jwtService.generateToken(fakeId, req.getEmail(), Collections.emptyList())).thenReturn("jwt-token");

        RegisterResponse response = userService.register(req);
        assertEquals(fakeId, response.userId());
        assertEquals("jwt-token", response.token());

        InOrder inOrder = inOrder(userRepository, passwordEncoder, userRepository, jwtService);
        inOrder.verify(userRepository).existsByEmail(req.getEmail());
        inOrder.verify(passwordEncoder).encode(req.getPassword());
        inOrder.verify(userRepository).save(any(User.class));
        inOrder.verify(jwtService).generateToken(fakeId, req.getEmail(), Collections.emptyList());
        verifyNoMoreInteractions(userRepository, passwordEncoder, jwtService);
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
    void login_NonExistingEmail_ThrowsResourceNotFoundException() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("password");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));

        verify(userRepository).findByEmail(request.getEmail());
        verifyNoMoreInteractions(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void login_InvalidPassword_ThrowsAuthenticationException() {
        // Arrange
        String email = "user@example.com";
        String rawPassword = "wrongPass";
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(rawPassword);
        User user = User.build("Ramesh Kumar", email, "hashedPwd", null);
        user.setId(UUID.randomUUID());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));

        InOrder inOrder = inOrder(userRepository, passwordEncoder);
        inOrder.verify(userRepository).findByEmail(email);
        inOrder.verify(passwordEncoder).matches(rawPassword, user.getPassword());
        verifyNoMoreInteractions(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void login_ValidCredentials_ReturnsLoginResponse() {
        // Arrange
        String email = "jane.doe@example.com";
        String rawPassword = "correctPass";
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(rawPassword);
        UUID userId = UUID.randomUUID();
        User user = User.build("Jane Doe", email, "hashedPwd", null);
        user.setId(userId);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, "hashedPwd")).thenReturn(true);
        when(jwtService.generateToken(userId, email, Collections.emptyList())).thenReturn("jwt-token");

        // Act
        LoginResponse response = userService.login(request);

        // Assert
        assertEquals(userId, response.userId());
        assertEquals("jwt-token", response.token());

        InOrder inOrder = inOrder(userRepository, passwordEncoder, jwtService);
        inOrder.verify(userRepository).findByEmail(email);
        inOrder.verify(passwordEncoder).matches(rawPassword, "hashedPwd");
        inOrder.verify(jwtService).generateToken(userId, email, Collections.emptyList());
        verifyNoMoreInteractions(userRepository, passwordEncoder, jwtService);
    }

}
