package com.enterprise.erp.roles.application.usecase;

import com.enterprise.erp.roles.application.dto.RoleResponse;
import com.enterprise.erp.roles.application.dto.UpdateRoleRequest;
import com.enterprise.erp.roles.application.mapper.RoleMapper;
import com.enterprise.erp.roles.infrastructure.persistence.RoleJpaEntity;
import com.enterprise.erp.roles.infrastructure.persistence.RoleJpaRepository;
import com.enterprise.erp.shared.application.constants.ApiErrorCode;
import com.enterprise.erp.shared.domain.exception.BusinessRuleException;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateRoleUseCase {

    private final RoleJpaRepository roleJpaRepository;
    private final RoleMapper roleMapper;

    @Transactional
    public RoleResponse execute(Long id, UpdateRoleRequest request) {
        RoleJpaEntity entity = roleJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", id));

        if (request.name() != null && !request.name().isBlank()) {
            roleJpaRepository.findByName(request.name())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new BusinessRuleException(ApiErrorCode.ROLE_ALREADY_EXISTS);
                    });
            entity.setName(request.name());
        }
        if (request.description() != null) {
            entity.setDescription(request.description());
        }
        if (request.active() != null) {
            entity.setActive(request.active());
        }

        return roleMapper.toResponse(roleJpaRepository.save(entity));
    }
}
