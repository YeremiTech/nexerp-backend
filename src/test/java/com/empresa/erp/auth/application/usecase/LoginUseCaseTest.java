package com.empresa.erp.auth.application.usecase;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginUseCaseTest {

    @Test
    void hashToken_shouldReturnConsistentSha256Hex() {
        String token = "sample-refresh-token";
        String hash1 = LoginUseCase.hashToken(token);
        String hash2 = LoginUseCase.hashToken(token);

        assertThat(hash1).isEqualTo(hash2);
        assertThat(hash1).hasSize(64);
        assertThat(hash1).matches("[0-9a-f]{64}");
    }

    @Test
    void hashToken_shouldDifferForDifferentTokens() {
        assertThat(LoginUseCase.hashToken("token-a"))
                .isNotEqualTo(LoginUseCase.hashToken("token-b"));
    }
}
