package com.enterprise.erp.auth.application.usecase;

import com.enterprise.erp.auth.application.dto.UpdateProfileRequest;
import com.enterprise.erp.shared.application.constants.ApiErrorCode;
import com.enterprise.erp.shared.domain.exception.BusinessRuleException;
import com.enterprise.erp.users.application.mapper.UserMapper;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaEntity;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
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
