package com.enterprise.erp.users.application.usecase;

import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import com.enterprise.erp.users.application.dto.UserResponse;
import com.enterprise.erp.users.application.mapper.UserMapper;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaEntity;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivateUserUseCase {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse execute(Long id, boolean active) {
        UserJpaEntity entity = userJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        entity.setActive(active);
        return userMapper.toResponse(userJpaRepository.save(entity));
    }
}
