package com.empresa.erp.ventas.application.usecase;

import com.empresa.erp.productos.application.mapper.ProductMapper;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaRepository;
import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaEntity;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaRepository;
import com.empresa.erp.ventas.application.dto.AddCartItemRequest;
import com.empresa.erp.ventas.application.dto.CartResponse;
import com.empresa.erp.ventas.application.mapper.SalesMapper;
import com.empresa.erp.ventas.infrastructure.persistence.SalesCartItemJpaEntity;
import com.empresa.erp.ventas.infrastructure.persistence.SalesCartJpaEntity;
import com.empresa.erp.ventas.infrastructure.persistence.SalesCartJpaRepository;
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
