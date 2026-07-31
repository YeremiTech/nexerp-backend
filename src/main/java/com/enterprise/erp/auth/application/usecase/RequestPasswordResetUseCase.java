package com.enterprise.erp.auth.application.usecase;

import com.enterprise.erp.auth.application.dto.PasswordResetRequestDto;
import com.enterprise.erp.auth.application.dto.PasswordResetResponseDto;
import com.enterprise.erp.auth.infrastructure.mail.EmailService;
import com.enterprise.erp.auth.infrastructure.persistence.PasswordResetTokenJpaEntity;
import com.enterprise.erp.auth.infrastructure.persistence.PasswordResetTokenJpaRepository;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestPasswordResetUseCase {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserJpaRepository userJpaRepository;
    private final PasswordResetTokenJpaRepository passwordResetTokenJpaRepository;
    private final EmailService emailService;

    @Value("${app.password-reset.code-expiration-minutes}")
    private int codeExpirationMinutes;

    @Transactional
    public PasswordResetResponseDto execute(PasswordResetRequestDto request) {
        userJpaRepository.findByEmail(request.email())
                .ifPresent(user -> {
                    String code = generateCode();
                    passwordResetTokenJpaRepository.save(PasswordResetTokenJpaEntity.builder()
                            .user(user)
                            .tokenHash(LoginUseCase.hashToken(code))
                            .expiresAt(LocalDateTime.now().plusMinutes(codeExpirationMinutes))
                            .used(false)
                            .build());
                    emailService.sendPasswordResetCode(user.getEmail(), code);
                });
        return PasswordResetResponseDto.submitted();
    }

    private String generateCode() {
        return String.format("%04d", RANDOM.nextInt(10_000));
    }
}
