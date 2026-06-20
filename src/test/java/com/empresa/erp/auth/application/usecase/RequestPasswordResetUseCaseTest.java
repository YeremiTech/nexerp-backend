package com.empresa.erp.auth.application.usecase;

import com.empresa.erp.auth.application.dto.PasswordResetRequestDto;
import com.empresa.erp.auth.application.dto.PasswordResetResponseDto;
import com.empresa.erp.auth.infrastructure.persistence.PasswordResetTokenJpaEntity;
import com.empresa.erp.auth.infrastructure.persistence.PasswordResetTokenJpaRepository;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaEntity;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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

    @InjectMocks
    private RequestPasswordResetUseCase useCase;

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
