package com.empresa.erp.auth.application.usecase;

import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import com.empresa.erp.usuarios.application.dto.UserResponse;
import com.empresa.erp.usuarios.application.mapper.UserMapper;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetProfileUseCase {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponse execute(String username) {
        return userJpaRepository.findByUsername(username)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new BusinessRuleException(ApiErrorCode.AUTH_USER_NOT_FOUND));
    }
}
