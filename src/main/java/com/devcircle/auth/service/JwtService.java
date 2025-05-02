package com.devcircle.auth.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final long expiryMs;
    private final Clock clock;
    private final JWTVerifier verifier;

    public JwtService(Algorithm algorithm, Long jwtExpiryMs, Clock clock) {
        this.algorithm = algorithm;
        this.expiryMs = jwtExpiryMs;
        this.clock = clock;
        this.verifier = JWT.require(algorithm).build();
    }

    /**
     * Issue a JWT where <strong>sub = userId</strong>.
     *
     * @param userId UUID (primary key in our DB)
     * @param email  current e‑mail address
     * @param roles  optional list of roles/scopes
     */
    public String generateToken(UUID userId,
            String email,
            Collection<String> roles) {

        Instant now = clock.instant();

        return JWT.create()
                .withSubject(userId.toString())
                .withClaim("email", email)
                .withArrayClaim("roles", roles.toArray(new String[0]))
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusMillis(expiryMs)))
                .sign(algorithm);
    }

    public DecodedJWT decode(String token) {
        return verifier.verify(token);
    }
}
