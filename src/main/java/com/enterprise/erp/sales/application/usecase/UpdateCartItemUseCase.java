package com.enterprise.erp.sales.application.usecase;

import com.enterprise.erp.sales.application.dto.CartResponse;
import com.enterprise.erp.sales.application.dto.UpdateCartItemRequest;
import com.enterprise.erp.sales.application.mapper.SalesMapper;
import com.enterprise.erp.sales.infrastructure.persistence.SalesCartItemJpaEntity;
import com.enterprise.erp.sales.infrastructure.persistence.SalesCartJpaEntity;
import com.enterprise.erp.sales.infrastructure.persistence.SalesCartJpaRepository;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCartItemUseCase {

    private final SalesCartJpaRepository salesCartJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final SalesMapper salesMapper;

    @Transactional
    public CartResponse execute(String username, Long itemId, UpdateCartItemRequest request) {
        SalesCartJpaEntity cart = getCart(username);
        SalesCartItemJpaEntity item = cart.getItems().stream()
                .filter(candidate -> candidate.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de carrito", itemId));
        item.setQuantity(request.quantity());
        SalesCartJpaEntity saved = salesCartJpaRepository.save(cart);
        return salesMapper.toCartResponse(
                salesCartJpaRepository.findByIdWithItems(saved.getId()).orElse(saved));
    }

    private SalesCartJpaEntity getCart(String username) {
        var user = userJpaRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", username));
        return salesCartJpaRepository.findLatestByUserIdWithItems(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Carrito", username));
    }
}
