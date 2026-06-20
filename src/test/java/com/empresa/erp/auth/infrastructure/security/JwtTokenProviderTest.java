package com.empresa.erp.auth.infrastructure.security;

import com.empresa.erp.infrastructure.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-secret-key-with-enough-length-for-hmac-sha256");
        properties.setAccessExpirationMs(3600000L);
        jwtTokenProvider = new JwtTokenProvider(properties);
    }

    @Test
    void generateAccessToken_shouldContainUsernameAndAuthorities() {
        String token = jwtTokenProvider.generateAccessToken("admin", "USR-000001", List.of("USER_READ", "USER_WRITE"));

        assertThat(jwtTokenProvider.extractUsername(token)).isEqualTo("admin");
        assertThat(jwtTokenProvider.extractUserCode(token)).isEqualTo("USR-000001");
        assertThat(jwtTokenProvider.extractAuthorities(token)).containsExactly("USER_READ", "USER_WRITE");
        assertThat(jwtTokenProvider.isTokenValid(token, "admin")).isTrue();
    }

    @Test
    void isTokenValid_shouldRejectWrongUsername() {
        String token = jwtTokenProvider.generateAccessToken("admin", "USR-000001", List.of("USER_READ"));

        assertThat(jwtTokenProvider.isTokenValid(token, "other")).isFalse();
    }
}
