package com.example.socio.security;

import com.example.socio.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final SecretKey key;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public JwtTokenProvider(@Value("${jwt.secret}") String jwtSecret) {
        this.key = decodeAndValidateSecretKey(jwtSecret);
    }

    private SecretKey decodeAndValidateSecretKey(String jwtSecret) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);

            if (decodedKey.length < 64) {
                throw new IllegalArgumentException("The secret key must be at least 512 bits (64 bytes) for HS512.");
            }

            return Keys.hmacShaKeyFor(decodedKey);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Base64-encoded secret key or insufficient key size. Please check your configuration.", e);
        }
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRole());
        claims.put("sub", user.getEmail());
        claims.put("userId", user.getId()); // Add userId to claims
        claims.put("visibility", user.getProfileVisibility());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public Long getUserIdFromToken(String token) {
        return getClaimsFromToken(token).get("userId", Long.class); // Extract userId
    }

    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject(); // Email is stored as the subject
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}