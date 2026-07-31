package com.enterprise.erp.users.application.usecase;

import com.enterprise.erp.roles.infrastructure.persistence.RoleJpaRepository;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import com.enterprise.erp.users.application.dto.UpdateUserRequest;
import com.enterprise.erp.users.application.dto.UserResponse;
import com.enterprise.erp.users.application.mapper.UserMapper;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaEntity;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse execute(Long id, UpdateUserRequest request) {
        UserJpaEntity entity = userJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        if (request.email() != null) {
            entity.setEmail(request.email());
        }
        if (request.password() != null) {
            entity.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        if (request.roleIds() != null) {
            entity.setRoles(new HashSet<>(roleJpaRepository.findAllById(request.roleIds())));
        }

        return userMapper.toResponse(userJpaRepository.save(entity));
    }
}
