package com.enterprise.erp.users.application.usecase;

import com.enterprise.erp.shared.util.ListSearchSupport;
import com.enterprise.erp.shared.util.PageableSupport;
import com.enterprise.erp.users.application.dto.UserResponse;
import com.enterprise.erp.users.application.mapper.UserMapper;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
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
