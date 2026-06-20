package com.empresa.erp.auth.application.usecase;

import com.empresa.erp.auth.application.dto.RefreshTokenRequest;
import com.empresa.erp.auth.application.dto.TokenResponse;
import com.empresa.erp.auth.infrastructure.persistence.RefreshTokenJpaEntity;
import com.empresa.erp.auth.infrastructure.persistence.RefreshTokenJpaRepository;
import com.empresa.erp.auth.infrastructure.security.JwtTokenProvider;
import com.empresa.erp.infrastructure.config.JwtProperties;
import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.util.ApiDisplayFormatter;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaEntity;
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
