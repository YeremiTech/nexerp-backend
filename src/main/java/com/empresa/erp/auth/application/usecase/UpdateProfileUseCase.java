package com.empresa.erp.auth.application.usecase;

import com.empresa.erp.auth.application.dto.UpdateProfileRequest;
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
public class UpdateProfileUseCase {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse execute(String username, UpdateProfileRequest request) {
        var entity = userJpaRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessRuleException(ApiErrorCode.AUTH_USER_NOT_FOUND));

        if (userJpaRepository.existsByEmailAndIdNot(request.email(), entity.getId())) {
            throw new BusinessRuleException(ApiErrorCode.EMAIL_ALREADY_EXISTS);
        }

        entity.setEmail(request.email().trim());
        return userMapper.toResponse(userJpaRepository.save(entity));
    }
}
