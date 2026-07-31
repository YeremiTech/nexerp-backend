package com.enterprise.erp.auth.application.usecase;

import com.enterprise.erp.auth.application.dto.SessionActivityRequest;
import com.enterprise.erp.auth.application.dto.SessionActivityResponse;
import com.enterprise.erp.auth.domain.UserPresenceResolver;
import com.enterprise.erp.auth.domain.UserPresenceStatus;
import com.enterprise.erp.auth.infrastructure.persistence.RefreshTokenJpaEntity;
import com.enterprise.erp.auth.infrastructure.persistence.RefreshTokenJpaRepository;
import com.enterprise.erp.auth.infrastructure.security.JwtTokenProvider;
import com.enterprise.erp.infrastructure.config.JwtProperties;
import com.enterprise.erp.infrastructure.config.SessionPresenceProperties;
import com.enterprise.erp.shared.application.constants.ApiErrorCode;
import com.enterprise.erp.shared.domain.exception.BusinessRuleException;
import com.enterprise.erp.shared.util.ApiDisplayFormatter;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionActivityUseCase {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final SessionPresenceProperties sessionPresenceProperties;

    @Transactional
    public SessionActivityResponse execute(String username, SessionActivityRequest request) {
        RefreshTokenJpaEntity refreshToken = refreshTokenJpaRepository
                .findByTokenHashAndRevokedFalse(LoginUseCase.hashToken(request.refreshToken()))
                .orElseThrow(() -> new BusinessRuleException(ApiErrorCode.AUTH_REFRESH_INVALID));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshToken.setRevoked(true);
            refreshTokenJpaRepository.save(refreshToken);
            throw new BusinessRuleException(ApiErrorCode.AUTH_REFRESH_EXPIRED);
        }

        UserJpaEntity user = refreshToken.getUser();
        if (!user.getUsername().equals(username)) {
            throw new BusinessRuleException(ApiErrorCode.AUTH_REFRESH_INVALID);
        }

        LocalDateTime now = LocalDateTime.now();
        if (refreshToken.getLastActivityAt() == null) {
            refreshToken.setLastActivityAt(now);
        }

        long idleSeconds = Duration.between(refreshToken.getLastActivityAt(), now).getSeconds();
        UserPresenceStatus presenceStatus = UserPresenceResolver.resolve(
                idleSeconds,
                sessionPresenceProperties.getAwaySeconds(),
                sessionPresenceProperties.getDisconnectSeconds());

        if (presenceStatus == UserPresenceStatus.DISCONNECTED) {
            refreshToken.setRevoked(true);
            refreshTokenJpaRepository.save(refreshToken);
            throw new BusinessRuleException(ApiErrorCode.AUTH_SESSION_IDLE_TIMEOUT);
        }

        if (Boolean.TRUE.equals(request.active())) {
            refreshToken.setLastActivityAt(now);
            idleSeconds = 0;
            presenceStatus = UserPresenceStatus.AVAILABLE;
        }

        refreshTokenJpaRepository.save(refreshToken);

        String accessToken = null;
        Long expiresIn = null;
        if (Boolean.TRUE.equals(request.active())) {
            List<String> authorities = user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(permission -> permission.getCode())
                    .distinct()
                    .toList();

            accessToken = jwtTokenProvider.generateAccessToken(
                    user.getUsername(),
                    ApiDisplayFormatter.userCode(user.getId()),
                    authorities);
            expiresIn = jwtProperties.getAccessExpirationMs() / 1000;
        }

        return new SessionActivityResponse(
                presenceStatus,
                accessToken,
                expiresIn,
                idleSeconds,
                sessionPresenceProperties.getAwaySeconds(),
                sessionPresenceProperties.getDisconnectSeconds());
    }
}
