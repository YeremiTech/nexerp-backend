package com.enterprise.erp.auth.application.usecase;

import com.enterprise.erp.auth.application.dto.PasswordResetConfirmDto;
import com.enterprise.erp.auth.infrastructure.persistence.PasswordResetTokenJpaEntity;
import com.enterprise.erp.auth.infrastructure.persistence.PasswordResetTokenJpaRepository;
import com.enterprise.erp.shared.application.constants.ApiErrorCode;
import com.enterprise.erp.shared.domain.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResetPasswordUseCase {

    private final PasswordResetTokenJpaRepository passwordResetTokenJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(PasswordResetConfirmDto request) {
        PasswordResetTokenJpaEntity tokenEntity = passwordResetTokenJpaRepository
                .findByTokenHashAndUsedFalse(LoginUseCase.hashToken(request.token()))
                .orElseThrow(() -> new BusinessRuleException(ApiErrorCode.AUTH_TOKEN_INVALID));

        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException(ApiErrorCode.AUTH_TOKEN_EXPIRED);
        }

        var user = tokenEntity.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        tokenEntity.setUsed(true);
    }
}
