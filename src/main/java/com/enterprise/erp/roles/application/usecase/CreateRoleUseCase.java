package com.enterprise.erp.roles.application.usecase;

import com.enterprise.erp.roles.application.dto.CreateRoleRequest;
import com.enterprise.erp.roles.application.dto.RoleResponse;
import com.enterprise.erp.roles.application.mapper.RoleMapper;
import com.enterprise.erp.roles.infrastructure.persistence.RoleJpaEntity;
import com.enterprise.erp.roles.infrastructure.persistence.RoleJpaRepository;
import com.enterprise.erp.shared.application.constants.ApiErrorCode;
import com.enterprise.erp.shared.domain.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateRoleUseCase {

    private final RoleJpaRepository roleJpaRepository;
    private final RoleMapper roleMapper;

    @Transactional
    public RoleResponse execute(CreateRoleRequest request) {
        if (roleJpaRepository.findByName(request.name()).isPresent()) {
            throw new BusinessRuleException(ApiErrorCode.ROLE_ALREADY_EXISTS);
        }
        RoleJpaEntity entity = RoleJpaEntity.builder()
                .name(request.name())
                .description(request.description())
                .active(true)
                .build();
        return roleMapper.toResponse(roleJpaRepository.save(entity));
    }
}
