package com.enterprise.erp.auth.application.usecase;

import com.enterprise.erp.auth.application.dto.PasswordResetRequestDto;
import com.enterprise.erp.auth.application.dto.PasswordResetResponseDto;
import com.enterprise.erp.auth.infrastructure.mail.EmailService;
import com.enterprise.erp.auth.infrastructure.persistence.PasswordResetTokenJpaEntity;
import com.enterprise.erp.auth.infrastructure.persistence.PasswordResetTokenJpaRepository;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaEntity;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestPasswordResetUseCaseTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private PasswordResetTokenJpaRepository passwordResetTokenJpaRepository;

    private RequestPasswordResetUseCase useCase;

    @BeforeEach
    void setUp() {
        EmailService emailService = new EmailService(null) {
            @Override
            public void sendPasswordResetCode(String to, String code) {
            }
        };
        useCase = new RequestPasswordResetUseCase(userJpaRepository, passwordResetTokenJpaRepository, emailService);
    }

    @Test
    void execute_shouldReturnGenericMessageWhenEmailNotFound() {
        when(userJpaRepository.findByEmail("missing@test.local")).thenReturn(Optional.empty());

        PasswordResetResponseDto response = useCase.execute(new PasswordResetRequestDto("missing@test.local"));

        assertThat(response.message()).isEqualTo(PasswordResetResponseDto.submitted().message());
        verify(passwordResetTokenJpaRepository, never()).save(any());
    }

    @Test
    void execute_shouldPersistHashedTokenWhenEmailExists() {
        UserJpaEntity user = UserJpaEntity.builder().id(1L).email("admin@localhost").username("admin").build();
        when(userJpaRepository.findByEmail("admin@localhost")).thenReturn(Optional.of(user));

        PasswordResetResponseDto response = useCase.execute(new PasswordResetRequestDto("admin@localhost"));

        ArgumentCaptor<PasswordResetTokenJpaEntity> captor = ArgumentCaptor.forClass(PasswordResetTokenJpaEntity.class);
        verify(passwordResetTokenJpaRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isEqualTo(user);
        assertThat(captor.getValue().getTokenHash()).matches("[0-9a-f]{64}");
        assertThat(captor.getValue().isUsed()).isFalse();
        assertThat(response.message()).doesNotContain("Token");
    }
}
