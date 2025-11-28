package com.entrelinhas.backend.security;

import com.entrelinhas.backend.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties properties;

    @PostConstruct
    void validateSecretLength() {
        int size = properties.secret().getBytes(StandardCharsets.UTF_8).length;
        if (size < 32) {
            throw new IllegalArgumentException("security.jwt.secret must be at least 32 bytes (UTF-8) long");
        }
    }

    public String generateToken(UserPrincipal principal) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(properties.expiration());
        return Jwts.builder()
                .subject(principal.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claims(Map.of(
                        "uid", principal.getId(),
                        "role", principal.getUserType().name()
                ))
                .signWith(getKey())
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }
}
