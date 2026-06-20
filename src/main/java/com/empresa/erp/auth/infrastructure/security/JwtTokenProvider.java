package com.empresa.erp.auth.infrastructure.security;

import com.empresa.erp.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        validateSecret(jwtProperties.getSecret());
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String username, String userCode, List<String> authorities) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getAccessExpirationMs());
        var builder = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(username)
                .claim("authorities", authorities)
                .issuedAt(now)
                .expiration(expiry);
        if (userCode != null && !userCode.isBlank()) {
            builder.claim("userCode", userCode);
        }
        return builder.signWith(secretKey).compact();
    }

    public String extractUserCode(String token) {
        return extractClaim(token, claims -> claims.get("userCode", String.class));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTokenId(String token) {
        return extractClaim(token, Claims::getId);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractAuthorities(String token) {
        return extractClaim(token, claims -> (List<String>) claims.get("authorities"));
    }

    public boolean isTokenValid(String token, String username) {
        String tokenUsername = extractUsername(token);
        return tokenUsername.equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }

    private void validateSecret(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET es obligatorio");
        }
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 32 bytes");
        }
        String normalized = secret.toLowerCase();
        if (normalized.contains("change-me") || normalized.contains("default") || normalized.contains("secret-key-here")) {
            throw new IllegalStateException("JWT_SECRET no debe usar valores por defecto");
        }
    }
}
