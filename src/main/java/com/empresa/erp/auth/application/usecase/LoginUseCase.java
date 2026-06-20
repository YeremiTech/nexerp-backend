package com.empresa.erp.auth.application.usecase;

import com.empresa.erp.auth.application.dto.LoginRequest;
import com.empresa.erp.auth.application.dto.TokenResponse;
import com.empresa.erp.auth.infrastructure.persistence.RefreshTokenJpaEntity;
import com.empresa.erp.auth.infrastructure.persistence.RefreshTokenJpaRepository;
import com.empresa.erp.auth.infrastructure.security.JwtTokenProvider;
import com.empresa.erp.infrastructure.config.JwtProperties;
import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.util.ApiDisplayFormatter;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaEntity;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final AuthenticationManager authenticationManager;
    private final UserJpaRepository userJpaRepository;
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    @Transactional
    public TokenResponse execute(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        UserJpaEntity user = userJpaRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new BusinessRuleException(ApiErrorCode.AUTH_USER_NOT_FOUND));

        List<String> authorities = authentication.getAuthorities().stream()
                .map(Object::toString)
                .toList();

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getUsername(),
                ApiDisplayFormatter.userCode(user.getId()),
                authorities);
        String refreshToken = UUID.randomUUID().toString();

        LocalDateTime now = LocalDateTime.now();
        refreshTokenJpaRepository.save(RefreshTokenJpaEntity.builder()
                .user(user)
                .tokenHash(hashToken(refreshToken))
                .expiresAt(now.plusSeconds(jwtProperties.getRefreshExpirationMs() / 1000))
                .lastActivityAt(now)
                .revoked(false)
                .build());

        return new TokenResponse(accessToken, refreshToken, jwtProperties.getAccessExpirationMs() / 1000);
    }

    static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
