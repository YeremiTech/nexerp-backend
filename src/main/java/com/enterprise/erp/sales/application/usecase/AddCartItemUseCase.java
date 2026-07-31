package com.enterprise.erp.sales.application.usecase;

import com.enterprise.erp.products.application.mapper.ProductMapper;
import com.enterprise.erp.products.infrastructure.persistence.ProductJpaRepository;
import com.enterprise.erp.shared.application.constants.ApiErrorCode;
import com.enterprise.erp.shared.domain.exception.BusinessRuleException;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaEntity;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
import com.enterprise.erp.sales.application.dto.AddCartItemRequest;
import com.enterprise.erp.sales.application.dto.CartResponse;
import com.enterprise.erp.sales.application.mapper.SalesMapper;
import com.enterprise.erp.sales.infrastructure.persistence.SalesCartItemJpaEntity;
import com.enterprise.erp.sales.infrastructure.persistence.SalesCartJpaEntity;
import com.enterprise.erp.sales.infrastructure.persistence.SalesCartJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AddCartItemUseCase {

    private final SalesCartJpaRepository salesCartJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final ProductMapper productMapper;
    private final SalesMapper salesMapper;

    @Transactional
    public CartResponse execute(String username, AddCartItemRequest request) {
        var user = userJpaRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", username));

        var product = productJpaRepository.findByIdWithPrices(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", request.productId()));

        BigDecimal unitPrice = productMapper.currentPrice(product);
        if (unitPrice == null) {
            throw new BusinessRuleException(ApiErrorCode.PRODUCT_NO_ACTIVE_PRICE);
        }

        SalesCartJpaEntity cart = resolveUserCart(user);

        var existing = cart.getItems().stream()
                .filter(item -> item.getProduct() != null
                        && request.productId().equals(item.getProduct().getId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + request.quantity());
        } else {
            cart.getItems().add(SalesCartItemJpaEntity.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.quantity())
                    .unitPrice(unitPrice)
                    .build());
        }

        SalesCartJpaEntity saved = salesCartJpaRepository.save(cart);
        return salesMapper.toCartResponse(
                salesCartJpaRepository.findByIdWithItems(saved.getId()).orElse(saved));
    }

    private SalesCartJpaEntity resolveUserCart(UserJpaEntity user) {
        return salesCartJpaRepository.findLatestByUserIdWithItems(user.getId())
                .orElseGet(() -> createCartForUser(user));
    }

    private SalesCartJpaEntity createCartForUser(UserJpaEntity user) {
        try {
            SalesCartJpaEntity cart = salesCartJpaRepository.save(SalesCartJpaEntity.builder()
                    .user(user)
                    .items(new ArrayList<>())
                    .build());
            return salesCartJpaRepository.findByIdWithItems(cart.getId()).orElse(cart);
        } catch (DataIntegrityViolationException ex) {
            return salesCartJpaRepository.findLatestByUserIdWithItems(user.getId())
                    .orElseThrow(() -> ex);
        }
    }
}
