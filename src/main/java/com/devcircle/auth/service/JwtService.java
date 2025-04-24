package com.devcircle.auth.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Service
public class JwtService {
    private static final String SECRET = "devcircle-secret-123";
    private static final long EXPIRY_MS = 86400000;

    public String generateToken(String email) {
        return JWT.create().withSubject(email).withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRY_MS)).sign(Algorithm.HMAC256(SECRET));
    }
}
