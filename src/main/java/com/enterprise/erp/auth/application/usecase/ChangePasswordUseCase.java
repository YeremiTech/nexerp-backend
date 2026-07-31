package com.enterprise.erp.auth.application.usecase;

import com.enterprise.erp.auth.application.dto.ChangePasswordRequest;
import com.enterprise.erp.shared.application.constants.ApiErrorCode;
import com.enterprise.erp.shared.domain.exception.BusinessRuleException;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangePasswordUseCase {

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(String username, ChangePasswordRequest request) {
        var user = userJpaRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessRuleException(ApiErrorCode.AUTH_USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BusinessRuleException(ApiErrorCode.AUTH_WRONG_PASSWORD);
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    }
}
