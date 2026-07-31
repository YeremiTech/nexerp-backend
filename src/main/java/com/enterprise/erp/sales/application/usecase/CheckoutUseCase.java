package com.enterprise.erp.sales.application.usecase;

import com.enterprise.erp.clients.infrastructure.persistence.ClientJpaRepository;
import com.enterprise.erp.inventory.application.dto.InventoryMovementRequest;
import com.enterprise.erp.inventory.application.usecase.ExitInventoryUseCase;
import com.enterprise.erp.inventory.infrastructure.persistence.WarehouseJpaRepository;
import com.enterprise.erp.shared.application.constants.ApiErrorCode;
import com.enterprise.erp.shared.domain.exception.BusinessRuleException;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
import com.enterprise.erp.sales.application.dto.CheckoutRequest;
import com.enterprise.erp.sales.application.dto.SalesOrderResponse;
import com.enterprise.erp.sales.application.mapper.SalesMapper;
import com.enterprise.erp.sales.infrastructure.persistence.*;
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
    private final WarehouseJpaRepository warehouseJpaRepository;
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
        var warehouse = warehouseJpaRepository.findById(request.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Almacén", request.warehouseId()));

        BigDecimal total = BigDecimal.ZERO;
        SalesOrderJpaEntity order = SalesOrderJpaEntity.builder()
                .client(client)
                .user(user)
                .warehouse(warehouse)
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
