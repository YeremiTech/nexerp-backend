package com.enterprise.erp.roles.application.usecase;

import com.enterprise.erp.roles.application.dto.RoleResponse;
import com.enterprise.erp.roles.application.mapper.RoleMapper;
import com.enterprise.erp.roles.infrastructure.persistence.RoleJpaRepository;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetRoleUseCase {

    private final RoleJpaRepository roleJpaRepository;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public RoleResponse execute(Long id) {
        return roleJpaRepository.findById(id)
                .map(roleMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", id));
    }
}
