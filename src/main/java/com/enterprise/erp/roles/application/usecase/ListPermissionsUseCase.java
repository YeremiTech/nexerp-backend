package com.enterprise.erp.roles.application.usecase;

import com.enterprise.erp.roles.application.dto.PermissionResponse;
import com.enterprise.erp.roles.infrastructure.persistence.PermissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListPermissionsUseCase {

    private final PermissionJpaRepository permissionJpaRepository;

    @Transactional(readOnly = true)
    public List<PermissionResponse> execute() {
        return permissionJpaRepository.findAllByOrderByCodeAsc().stream()
                .map(p -> new PermissionResponse(p.getId(), p.getCode(), p.getDescription()))
                .toList();
    }
}
