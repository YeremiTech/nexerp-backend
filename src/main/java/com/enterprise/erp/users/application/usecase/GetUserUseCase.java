package com.enterprise.erp.users.application.usecase;

import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import com.enterprise.erp.users.application.dto.UserResponse;
import com.enterprise.erp.users.application.mapper.UserMapper;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUserUseCase {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponse execute(Long id) {
        return userJpaRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }
}
