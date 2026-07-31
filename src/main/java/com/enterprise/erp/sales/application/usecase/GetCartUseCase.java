package com.enterprise.erp.sales.application.usecase;

import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
import com.enterprise.erp.sales.application.dto.CartResponse;
import com.enterprise.erp.sales.application.mapper.SalesMapper;
import com.enterprise.erp.sales.infrastructure.persistence.SalesCartJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetCartUseCase {

    private final SalesCartJpaRepository salesCartJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final SalesMapper salesMapper;

    @Transactional(readOnly = true)
    public CartResponse execute(String username) {
        var user = userJpaRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", username));
        return salesCartJpaRepository.findLatestByUserIdWithItems(user.getId())
                .map(salesMapper::toCartResponse)
                .orElse(new CartResponse(null, null, List.of(), BigDecimal.ZERO));
    }
}
