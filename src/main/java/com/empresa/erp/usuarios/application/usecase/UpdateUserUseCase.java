package com.empresa.erp.usuarios.application.usecase;

import com.empresa.erp.roles.infrastructure.persistence.RoleJpaRepository;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
import com.empresa.erp.usuarios.application.dto.UpdateUserRequest;
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
