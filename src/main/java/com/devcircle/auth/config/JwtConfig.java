package com.devcircle.auth.config;

import java.time.Clock;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.auth0.jwt.algorithms.Algorithm;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret;
    private long expiryMs;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String s) {
        this.secret = s;
    }

    public long getExpiryMs() {
        return expiryMs;
    }

    public void setExpiryMs(long e) {
        this.expiryMs = e;
    }

    @Bean
    @Primary
    public Algorithm hmacAlgorithm() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret must be supplied via JWT_SECRET env var or yml/ props!");
        }
        return Algorithm.HMAC256(secret);
    }

    @Bean
    public long jwtExpiryMs() {
        return expiryMs;
    }

    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }

}
