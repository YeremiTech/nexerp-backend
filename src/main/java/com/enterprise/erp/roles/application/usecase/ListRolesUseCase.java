package com.enterprise.erp.roles.application.usecase;

import com.enterprise.erp.roles.application.dto.RoleResponse;
import com.enterprise.erp.roles.application.mapper.RoleMapper;
import com.enterprise.erp.roles.infrastructure.persistence.RoleJpaRepository;
import com.enterprise.erp.shared.util.PageableSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListRolesUseCase {

    private final RoleJpaRepository roleJpaRepository;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    public Page<RoleResponse> execute(Pageable pageable) {
        pageable = PageableSupport.newestFirst(pageable);
        return roleJpaRepository.findAll(pageable).map(roleMapper::toResponse);
    }
}
