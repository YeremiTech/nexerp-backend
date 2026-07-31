package com.enterprise.erp.auth.application.usecase;

import com.enterprise.erp.auth.application.dto.PasswordResetRequestDto;
import com.enterprise.erp.auth.application.dto.PasswordResetResponseDto;
import com.enterprise.erp.auth.infrastructure.persistence.PasswordResetTokenJpaEntity;
import com.enterprise.erp.auth.infrastructure.persistence.PasswordResetTokenJpaRepository;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestPasswordResetUseCase {

    private final UserJpaRepository userJpaRepository;
    private final PasswordResetTokenJpaRepository passwordResetTokenJpaRepository;

    @Transactional
    public PasswordResetResponseDto execute(PasswordResetRequestDto request) {
        userJpaRepository.findByEmail(request.email())
                .ifPresent(user -> {
                    String token = UUID.randomUUID().toString();
                    passwordResetTokenJpaRepository.save(PasswordResetTokenJpaEntity.builder()
                            .user(user)
                            .tokenHash(LoginUseCase.hashToken(token))
                            .expiresAt(LocalDateTime.now().plusHours(1))
                            .used(false)
                            .build());
                    // TODO: enviar token por email; nunca exponerlo en la respuesta HTTP.
                });
        return PasswordResetResponseDto.submitted();
    }
}
