package com.empresa.erp.auth.application.usecase;

import com.empresa.erp.auth.application.dto.UpdateProfileRequest;
import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import com.empresa.erp.usuarios.application.mapper.UserMapper;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaEntity;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProfileUseCaseTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UpdateProfileUseCase useCase;

    @Test
    void execute_shouldRejectDuplicateEmail() {
        var user = UserJpaEntity.builder().id(1L).username("admin").email("admin@test.local").build();
        when(userJpaRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(userJpaRepository.existsByEmailAndIdNot("otro@test.local", 1L)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute("admin", new UpdateProfileRequest("otro@test.local")))
                .isInstanceOf(BusinessRuleException.class)
                .extracting(ex -> ((BusinessRuleException) ex).getErrorCode())
                .isEqualTo(ApiErrorCode.EMAIL_ALREADY_EXISTS.getCode());

        verify(userJpaRepository, never()).save(any());
    }
}
