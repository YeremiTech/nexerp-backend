package com.empresa.erp.ventas.application.usecase;

import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaRepository;
import com.empresa.erp.ventas.application.dto.CartResponse;
import com.empresa.erp.ventas.application.mapper.SalesMapper;
import com.empresa.erp.ventas.infrastructure.persistence.SalesCartJpaRepository;
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
