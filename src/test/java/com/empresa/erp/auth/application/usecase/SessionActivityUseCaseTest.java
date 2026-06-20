package com.empresa.erp.auth.application.usecase;

import com.empresa.erp.auth.application.dto.SessionActivityRequest;
import com.empresa.erp.auth.application.dto.SessionActivityResponse;
import com.empresa.erp.auth.domain.UserPresenceStatus;
import com.empresa.erp.auth.infrastructure.persistence.RefreshTokenJpaEntity;
import com.empresa.erp.auth.infrastructure.persistence.RefreshTokenJpaRepository;
import com.empresa.erp.auth.infrastructure.security.JwtTokenProvider;
import com.empresa.erp.infrastructure.config.JwtProperties;
import com.empresa.erp.infrastructure.config.SessionPresenceProperties;
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
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionActivityUseCaseTest {

    @Mock
    private RefreshTokenJpaRepository refreshTokenJpaRepository;

    private SessionPresenceProperties sessionPresenceProperties;
    private JwtProperties jwtProperties;
    private JwtTokenProvider jwtTokenProvider;
    private SessionActivityUseCase useCase;

    @BeforeEach
    void setUp() {
        sessionPresenceProperties = new SessionPresenceProperties();
        sessionPresenceProperties.setAwaySeconds(210);
        sessionPresenceProperties.setDisconnectSeconds(420);

        jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-key-with-enough-length-for-hmac-sha256");
        jwtProperties.setAccessExpirationMs(900_000L);
        jwtProperties.setRefreshExpirationMs(604_800_000L);

        jwtTokenProvider = new JwtTokenProvider(jwtProperties);
        useCase = new SessionActivityUseCase(
                refreshTokenJpaRepository,
                jwtTokenProvider,
                jwtProperties,
                sessionPresenceProperties);
    }

    @Test
    void execute_shouldRefreshAccessTokenWhenUserIsActive() {
        UserJpaEntity user = activeUser();
        RefreshTokenJpaEntity token = validToken(user, LocalDateTime.now().minusSeconds(30));
        when(refreshTokenJpaRepository.findByTokenHashAndRevokedFalse(LoginUseCase.hashToken("refresh-token")))
                .thenReturn(Optional.of(token));
        when(refreshTokenJpaRepository.save(any(RefreshTokenJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SessionActivityResponse response = useCase.execute(
                "admin",
                new SessionActivityRequest("refresh-token", true));

        assertThat(response.presenceStatus()).isEqualTo(UserPresenceStatus.AVAILABLE);
        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.expiresIn()).isEqualTo(900L);
        assertThat(response.idleSeconds()).isZero();
        verify(refreshTokenJpaRepository).save(token);
    }

    @Test
    void execute_shouldReturnAwayWithoutRefreshingTokenWhenInactive() {
        UserJpaEntity user = activeUser();
        RefreshTokenJpaEntity token = validToken(user, LocalDateTime.now().minusSeconds(300));
        when(refreshTokenJpaRepository.findByTokenHashAndRevokedFalse(LoginUseCase.hashToken("refresh-token")))
                .thenReturn(Optional.of(token));
        when(refreshTokenJpaRepository.save(any(RefreshTokenJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SessionActivityResponse response = useCase.execute(
                "admin",
                new SessionActivityRequest("refresh-token", false));

        assertThat(response.presenceStatus()).isEqualTo(UserPresenceStatus.AWAY);
        assertThat(response.accessToken()).isNull();
        assertThat(response.expiresIn()).isNull();
        assertThat(response.idleSeconds()).isEqualTo(300L);
    }

    @Test
    void execute_shouldRevokeTokenWhenIdleTimeoutReached() {
        UserJpaEntity user = activeUser();
        RefreshTokenJpaEntity token = validToken(user, LocalDateTime.now().minusSeconds(500));
        when(refreshTokenJpaRepository.findByTokenHashAndRevokedFalse(LoginUseCase.hashToken("refresh-token")))
                .thenReturn(Optional.of(token));
        when(refreshTokenJpaRepository.save(any(RefreshTokenJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> useCase.execute("admin", new SessionActivityRequest("refresh-token", false)))
                .isInstanceOf(BusinessRuleException.class)
                .extracting(ex -> ((BusinessRuleException) ex).getErrorCode())
                .isEqualTo(ApiErrorCode.AUTH_SESSION_IDLE_TIMEOUT.getCode());

        assertThat(token.isRevoked()).isTrue();
    }

    @Test
    void execute_shouldNotReactivateSessionAfterIdleTimeout() {
        UserJpaEntity user = activeUser();
        RefreshTokenJpaEntity token = validToken(user, LocalDateTime.now().minusSeconds(500));
        when(refreshTokenJpaRepository.findByTokenHashAndRevokedFalse(LoginUseCase.hashToken("refresh-token")))
                .thenReturn(Optional.of(token));
        when(refreshTokenJpaRepository.save(any(RefreshTokenJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> useCase.execute("admin", new SessionActivityRequest("refresh-token", true)))
                .isInstanceOf(BusinessRuleException.class)
                .extracting(ex -> ((BusinessRuleException) ex).getErrorCode())
                .isEqualTo(ApiErrorCode.AUTH_SESSION_IDLE_TIMEOUT.getCode());

        assertThat(token.isRevoked()).isTrue();
    }

    private static UserJpaEntity activeUser() {
        PermissionJpaEntity permission = PermissionJpaEntity.builder().code("CLIENT_READ").build();
        RoleJpaEntity role = RoleJpaEntity.builder().name("ADMIN").permissions(Set.of(permission)).build();
        return UserJpaEntity.builder()
                .id(1L)
                .username("admin")
                .roles(Set.of(role))
                .build();
    }

    private static RefreshTokenJpaEntity validToken(UserJpaEntity user, LocalDateTime lastActivityAt) {
        return RefreshTokenJpaEntity.builder()
                .user(user)
                .tokenHash(LoginUseCase.hashToken("refresh-token"))
                .expiresAt(LocalDateTime.now().plusHours(1))
                .lastActivityAt(lastActivityAt)
                .revoked(false)
                .build();
    }
}
