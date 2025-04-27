package com.devcircle.auth.dto;

import java.util.UUID;

public record LoginResponse(UUID userId, String token) {

}
