package com.empresa.erp.usuarios.application.usecase;

import com.empresa.erp.shared.util.ListSearchSupport;
import com.empresa.erp.shared.util.PageableSupport;
import com.empresa.erp.usuarios.application.dto.UserResponse;
import com.empresa.erp.usuarios.application.mapper.UserMapper;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListUsersUseCase {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Page<UserResponse> execute(String search, Boolean active, Pageable pageable) {
        pageable = PageableSupport.newestFirst(pageable);
        return userJpaRepository.search(
                ListSearchSupport.toLikePattern(search),
                active,
                pageable
        ).map(userMapper::toResponse);
    }
}
