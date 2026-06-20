package com.empresa.erp.roles.application.usecase;

import com.empresa.erp.roles.application.dto.AssignPermissionsRequest;
import com.empresa.erp.roles.application.dto.RoleResponse;
import com.empresa.erp.roles.application.mapper.RoleMapper;
import com.empresa.erp.roles.infrastructure.persistence.PermissionJpaRepository;
import com.empresa.erp.roles.infrastructure.persistence.RoleJpaEntity;
import com.empresa.erp.roles.infrastructure.persistence.RoleJpaRepository;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AssignPermissionsUseCase {

    private final RoleJpaRepository roleJpaRepository;
    private final PermissionJpaRepository permissionJpaRepository;
    private final RoleMapper roleMapper;

    @Transactional
    public RoleResponse execute(Long roleId, AssignPermissionsRequest request) {
        RoleJpaEntity entity = roleJpaRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", roleId));
        entity.setPermissions(new HashSet<>(permissionJpaRepository.findAllById(request.permissionIds())));
        return roleMapper.toResponse(roleJpaRepository.save(entity));
    }
}
