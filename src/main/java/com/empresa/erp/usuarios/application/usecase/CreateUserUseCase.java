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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class CreateUserUseCase {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse execute(CreateUserRequest request) {
        if (userJpaRepository.existsByUsername(request.username())) {
            throw new BusinessRuleException(ApiErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userJpaRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException(ApiErrorCode.EMAIL_ALREADY_EXISTS);
        }

        var roles = request.roleIds() == null ? new HashSet<RoleJpaEntity>()
                : new HashSet<>(roleJpaRepository.findAllById(request.roleIds()));

        UserJpaEntity entity = UserJpaEntity.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .active(true)
                .roles(roles)
                .build();

        return userMapper.toResponse(userJpaRepository.save(entity));
    }
}
