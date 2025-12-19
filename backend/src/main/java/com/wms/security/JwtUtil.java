package com.wms.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT utility for generating and validating tokens.
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtUtil(
            @Value("${jwt.secret:default-dev-secret-key-must-be-at-least-256-bits}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        // Ensure key is at least 256 bits (32 bytes)
        String paddedSecret = secret;
        while (paddedSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
            paddedSecret = paddedSecret + paddedSecret;
        }
        this.secretKey = Keys.hmacShaKeyFor(paddedSecret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Generate a JWT token for the given username and role.
     */
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extract username from token.
     */
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extract role from token.
     */
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * Validate the token.
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
