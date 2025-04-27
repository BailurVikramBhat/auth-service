package com.devcircle.auth.dto;

import java.util.UUID;

public record RegisterResponse(UUID userId, String token, String message) {
}
