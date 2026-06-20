package com.empresa.erp.roles.application.usecase;

import com.empresa.erp.roles.application.dto.CreateRoleRequest;
import com.empresa.erp.roles.application.dto.RoleResponse;
import com.empresa.erp.roles.application.mapper.RoleMapper;
import com.empresa.erp.roles.infrastructure.persistence.RoleJpaEntity;
import com.empresa.erp.roles.infrastructure.persistence.RoleJpaRepository;
import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
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
