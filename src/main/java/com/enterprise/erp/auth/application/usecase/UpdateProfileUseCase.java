package com.enterprise.erp.auth.application.usecase;

import com.enterprise.erp.auth.application.dto.UpdateProfileRequest;
import com.enterprise.erp.shared.application.constants.ApiErrorCode;
import com.enterprise.erp.shared.domain.exception.BusinessRuleException;
import com.enterprise.erp.users.application.dto.UserResponse;
import com.enterprise.erp.users.application.mapper.UserMapper;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
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
