package com.empresa.erp.auth.application.usecase;

import com.empresa.erp.auth.application.dto.RefreshTokenRequest;
import com.empresa.erp.auth.application.service.TokenRevocationService;
import com.empresa.erp.auth.infrastructure.persistence.RefreshTokenJpaRepository;
import com.empresa.erp.auth.infrastructure.security.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LogoutUseCase {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRevocationService tokenRevocationService;

    @Transactional
    public void execute(RefreshTokenRequest request, String authorizationHeader) {
        refreshTokenJpaRepository.findByTokenHashAndRevokedFalse(LoginUseCase.hashToken(request.refreshToken()))
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenJpaRepository.save(token);
                });
        revokeAccessToken(authorizationHeader);
    }

    private void revokeAccessToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return;
        }

        String accessToken = authorizationHeader.substring(7);
        try {
            String tokenId = jwtTokenProvider.extractTokenId(accessToken);
            Instant expiresAt = jwtTokenProvider.extractExpiration(accessToken).toInstant();
            tokenRevocationService.revoke(tokenId, expiresAt);
        } catch (JwtException | IllegalArgumentException ignored) {
            // Logout debe ser idempotente incluso si el access token ya no es válido.
        }
    }
}
