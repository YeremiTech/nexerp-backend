package com.enterprise.erp.auth.application.usecase;

import com.enterprise.erp.auth.application.dto.RefreshTokenRequest;
import com.enterprise.erp.auth.application.dto.TokenResponse;
import com.enterprise.erp.auth.infrastructure.persistence.RefreshTokenJpaEntity;
import com.enterprise.erp.auth.infrastructure.persistence.RefreshTokenJpaRepository;
import com.enterprise.erp.auth.infrastructure.security.JwtTokenProvider;
import com.enterprise.erp.infrastructure.config.JwtProperties;
import com.enterprise.erp.shared.application.constants.ApiErrorCode;
import com.enterprise.erp.shared.util.ApiDisplayFormatter;
import com.enterprise.erp.shared.domain.exception.BusinessRuleException;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    @Transactional
    public TokenResponse execute(RefreshTokenRequest request) {
        RefreshTokenJpaEntity existing = refreshTokenJpaRepository
                .findByTokenHashAndRevokedFalse(LoginUseCase.hashToken(request.refreshToken()))
                .orElseThrow(() -> new BusinessRuleException(ApiErrorCode.AUTH_REFRESH_INVALID));

        if (existing.getExpiresAt().isBefore(LocalDateTime.now())) {
            existing.setRevoked(true);
            refreshTokenJpaRepository.save(existing);
            throw new BusinessRuleException(ApiErrorCode.AUTH_REFRESH_EXPIRED);
        }

        UserJpaEntity user = existing.getUser();
        List<String> authorities = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getCode())
                .distinct()
                .toList();

        existing.setRevoked(true);
        refreshTokenJpaRepository.save(existing);

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getUsername(),
                ApiDisplayFormatter.userCode(user.getId()),
                authorities);
        String refreshToken = UUID.randomUUID().toString();

        LocalDateTime now = LocalDateTime.now();
        refreshTokenJpaRepository.save(RefreshTokenJpaEntity.builder()
                .user(user)
                .tokenHash(LoginUseCase.hashToken(refreshToken))
                .expiresAt(now.plusSeconds(jwtProperties.getRefreshExpirationMs() / 1000))
                .lastActivityAt(now)
                .revoked(false)
                .build());

        return new TokenResponse(accessToken, refreshToken, jwtProperties.getAccessExpirationMs() / 1000);
    }
}
