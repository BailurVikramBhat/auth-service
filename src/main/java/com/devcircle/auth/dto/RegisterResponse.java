package com.devcircle.auth.dto;

import java.util.UUID;

public record RegisterResponse(UUID uuid, String token, String message) {
}
