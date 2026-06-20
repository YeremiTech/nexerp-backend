package com.empresa.erp.ventas.application.usecase;

import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaRepository;
import com.empresa.erp.inventario.application.dto.InventoryMovementRequest;
import com.empresa.erp.inventario.application.usecase.ExitInventoryUseCase;
import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaRepository;
import com.empresa.erp.ventas.application.dto.CheckoutRequest;
import com.empresa.erp.ventas.application.dto.SalesOrderResponse;
import com.empresa.erp.ventas.application.mapper.SalesMapper;
import com.empresa.erp.ventas.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutUseCase {

    private final SalesCartJpaRepository salesCartJpaRepository;
    private final SalesOrderJpaRepository salesOrderJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ClientJpaRepository clientJpaRepository;
    private final ExitInventoryUseCase exitInventoryUseCase;
    private final SalesMapper salesMapper;

    @Transactional
    public SalesOrderResponse execute(String username, CheckoutRequest request) {
        var user = userJpaRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", username));

        SalesCartJpaEntity cart = salesCartJpaRepository.findLatestByUserIdWithItems(user.getId())
                .orElseThrow(() -> new BusinessRuleException(ApiErrorCode.EMPTY_CART));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BusinessRuleException(ApiErrorCode.EMPTY_CART);
        }

        var client = clientJpaRepository.findById(request.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.clientId()));

        BigDecimal total = BigDecimal.ZERO;
        SalesOrderJpaEntity order = SalesOrderJpaEntity.builder()
                .client(client)
                .user(user)
                .status("COMPLETED")
                .total(BigDecimal.ZERO)
                .build();

        List<SalesCartItemJpaEntity> cartItems = new ArrayList<>(cart.getItems());
        for (var cartItem : cartItems) {
            order.getLines().add(SalesOrderLineJpaEntity.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .build());
            total = total.add(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        order.setTotal(total);

        SalesOrderJpaEntity saved = salesOrderJpaRepository.save(order);

        for (var cartItem : cartItems) {
            exitInventoryUseCase.execute(new InventoryMovementRequest(
                    cartItem.getProduct().getId(),
                    request.warehouseId(),
                    cartItem.getQuantity(),
                    "SALES_ORDER",
                    saved.getId()));
        }

        cart.getItems().clear();
        salesCartJpaRepository.save(cart);

        return salesMapper.toOrderResponse(saved);
    }
}
