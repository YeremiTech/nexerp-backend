package com.empresa.erp.usuarios.application.usecase;

import com.empresa.erp.roles.infrastructure.persistence.RoleJpaEntity;
import com.empresa.erp.roles.infrastructure.persistence.RoleJpaRepository;
import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import com.empresa.erp.usuarios.application.dto.CreateUserRequest;
import com.empresa.erp.usuarios.application.dto.UserResponse;
import com.empresa.erp.usuarios.application.mapper.UserMapper;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaEntity;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private RoleJpaRepository roleJpaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CreateUserUseCase useCase;

    @Test
    void execute_shouldRejectDuplicatedUsername() {
        CreateUserRequest request = new CreateUserRequest("admin", "admin@example.com", "password123", Set.of(1L));
        when(userJpaRepository.existsByUsername("admin")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(BusinessRuleException.class)
                .extracting(ex -> ((BusinessRuleException) ex).getErrorCode())
                .isEqualTo(ApiErrorCode.USERNAME_ALREADY_EXISTS.getCode());
    }

    @Test
    void execute_shouldHashPasswordAndAttachRoles() {
        CreateUserRequest request = new CreateUserRequest("operator", "operator@example.com", "password123", Set.of(1L));
        RoleJpaEntity role = RoleJpaEntity.builder().id(1L).name("OPERATOR").active(true).build();
        UserJpaEntity saved = UserJpaEntity.builder().id(10L).username("operator").email("operator@example.com").active(true).build();
        UserResponse response = new UserResponse(10L, "U-000010", "operator", "operator@example.com", true, Set.of("OPERATOR"), null, null);

        when(userJpaRepository.existsByUsername("operator")).thenReturn(false);
        when(userJpaRepository.existsByEmail("operator@example.com")).thenReturn(false);
        when(roleJpaRepository.findAllById(Set.of(1L))).thenReturn(List.of(role));
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
        when(userJpaRepository.save(any(UserJpaEntity.class))).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(response);

        UserResponse result = useCase.execute(request);

        ArgumentCaptor<UserJpaEntity> captor = ArgumentCaptor.forClass(UserJpaEntity.class);
        verify(userJpaRepository).save(captor.capture());
        UserJpaEntity entity = captor.getValue();
        assertThat(entity.getPasswordHash()).isEqualTo("hashed-password");
        assertThat(entity.getRoles()).containsExactly(role);
        assertThat(entity.isActive()).isTrue();
        assertThat(result).isEqualTo(response);
    }
}
