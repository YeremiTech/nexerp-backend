package com.empresa.erp.auth.application.usecase;

import com.empresa.erp.auth.application.dto.RefreshTokenRequest;
import com.empresa.erp.auth.application.dto.TokenResponse;
import com.empresa.erp.auth.infrastructure.persistence.RefreshTokenJpaEntity;
import com.empresa.erp.auth.infrastructure.persistence.RefreshTokenJpaRepository;
import com.empresa.erp.auth.infrastructure.security.JwtTokenProvider;
import com.empresa.erp.infrastructure.config.JwtProperties;
import com.empresa.erp.roles.infrastructure.persistence.PermissionJpaEntity;
import com.empresa.erp.roles.infrastructure.persistence.RoleJpaEntity;
import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

    @Mock
    private RefreshTokenJpaRepository refreshTokenJpaRepository;

    private JwtProperties jwtProperties;
    private JwtTokenProvider jwtTokenProvider;
    private RefreshTokenUseCase useCase;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-key-with-enough-length-for-hmac-sha256");
        jwtProperties.setAccessExpirationMs(900_000L);
        jwtProperties.setRefreshExpirationMs(604_800_000L);
        jwtTokenProvider = new JwtTokenProvider(jwtProperties);
        useCase = new RefreshTokenUseCase(refreshTokenJpaRepository, jwtTokenProvider, jwtProperties);
    }

    @Test
    void execute_shouldRejectInvalidRefreshToken() {
        when(refreshTokenJpaRepository.findByTokenHashAndRevokedFalse(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new RefreshTokenRequest("bad-token")))
                .isInstanceOf(BusinessRuleException.class)
                .extracting(ex -> ((BusinessRuleException) ex).getErrorCode())
                .isEqualTo(ApiErrorCode.AUTH_REFRESH_INVALID.getCode());
    }

    @Test
    void execute_shouldRotateTokensWhenRefreshTokenValid() {
        PermissionJpaEntity permission = PermissionJpaEntity.builder().code("CLIENT_READ").build();
        RoleJpaEntity role = RoleJpaEntity.builder().name("ADMIN").permissions(Set.of(permission)).build();
        UserJpaEntity user = UserJpaEntity.builder()
                .username("admin")
                .roles(Set.of(role))
                .build();
        RefreshTokenJpaEntity existing = RefreshTokenJpaEntity.builder()
                .user(user)
                .tokenHash(LoginUseCase.hashToken("refresh-token"))
                .expiresAt(LocalDateTime.now().plusHours(1))
                .lastActivityAt(LocalDateTime.now())
                .revoked(false)
                .build();
        when(refreshTokenJpaRepository.findByTokenHashAndRevokedFalse(LoginUseCase.hashToken("refresh-token")))
                .thenReturn(Optional.of(existing));
        when(refreshTokenJpaRepository.save(any(RefreshTokenJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TokenResponse response = useCase.execute(new RefreshTokenRequest("refresh-token"));

        assertThat(response.accessToken()).isNotBlank();
        assertThat(jwtTokenProvider.extractUsername(response.accessToken())).isEqualTo("admin");
        assertThat(jwtTokenProvider.extractAuthorities(response.accessToken())).isEqualTo(List.of("CLIENT_READ"));
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.expiresIn()).isEqualTo(900L);
        assertThat(existing.isRevoked()).isTrue();
        verify(refreshTokenJpaRepository, times(2)).save(any(RefreshTokenJpaEntity.class));
    }
}
