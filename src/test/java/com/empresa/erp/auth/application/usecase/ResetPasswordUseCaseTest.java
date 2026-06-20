package com.empresa.erp.auth.application.usecase;

import com.empresa.erp.auth.application.dto.PasswordResetConfirmDto;
import com.empresa.erp.auth.infrastructure.persistence.PasswordResetTokenJpaEntity;
import com.empresa.erp.auth.infrastructure.persistence.PasswordResetTokenJpaRepository;
import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResetPasswordUseCaseTest {

    @Mock
    private PasswordResetTokenJpaRepository passwordResetTokenJpaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ResetPasswordUseCase useCase;

    @Test
    void execute_shouldRejectUnknownToken() {
        when(passwordResetTokenJpaRepository.findByTokenHashAndUsedFalse(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new PasswordResetConfirmDto("bad-token", "new-password1")))
                .isInstanceOf(BusinessRuleException.class)
                .extracting(ex -> ((BusinessRuleException) ex).getErrorCode())
                .isEqualTo(ApiErrorCode.AUTH_TOKEN_INVALID.getCode());
    }

    @Test
    void execute_shouldRejectExpiredToken() {
        UserJpaEntity user = UserJpaEntity.builder().username("admin").build();
        PasswordResetTokenJpaEntity token = PasswordResetTokenJpaEntity.builder()
                .user(user)
                .tokenHash(LoginUseCase.hashToken("valid-token"))
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .used(false)
                .build();
        when(passwordResetTokenJpaRepository.findByTokenHashAndUsedFalse(LoginUseCase.hashToken("valid-token")))
                .thenReturn(Optional.of(token));

        assertThatThrownBy(() -> useCase.execute(new PasswordResetConfirmDto("valid-token", "new-password1")))
                .isInstanceOf(BusinessRuleException.class)
                .extracting(ex -> ((BusinessRuleException) ex).getErrorCode())
                .isEqualTo(ApiErrorCode.AUTH_TOKEN_EXPIRED.getCode());
    }

    @Test
    void execute_shouldUpdatePasswordAndMarkTokenUsed() {
        UserJpaEntity user = UserJpaEntity.builder().username("admin").build();
        PasswordResetTokenJpaEntity token = PasswordResetTokenJpaEntity.builder()
                .user(user)
                .tokenHash(LoginUseCase.hashToken("valid-token"))
                .expiresAt(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();
        when(passwordResetTokenJpaRepository.findByTokenHashAndUsedFalse(LoginUseCase.hashToken("valid-token")))
                .thenReturn(Optional.of(token));
        when(passwordEncoder.encode("new-password1")).thenReturn("encoded-password");

        useCase.execute(new PasswordResetConfirmDto("valid-token", "new-password1"));

        assertThat(user.getPasswordHash()).isEqualTo("encoded-password");
        assertThat(token.isUsed()).isTrue();
        verify(passwordEncoder).encode("new-password1");
    }
}
