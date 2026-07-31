package com.enterprise.erp.sales.application.mapper;

import com.enterprise.erp.shared.util.ApiDisplayFormatter;
import com.enterprise.erp.sales.application.dto.CartResponse;
import com.enterprise.erp.sales.application.dto.SalesOrderListItem;
import com.enterprise.erp.sales.application.dto.SalesOrderResponse;
import com.enterprise.erp.sales.infrastructure.persistence.SalesCartJpaEntity;
import com.enterprise.erp.sales.infrastructure.persistence.SalesCartItemJpaEntity;
import com.enterprise.erp.sales.infrastructure.persistence.SalesOrderJpaEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class SalesMapper {

    public CartResponse toCartResponse(SalesCartJpaEntity cart) {
        var cartItems = cart.getItems() == null ? List.<SalesCartItemJpaEntity>of() : cart.getItems();
        var items = cartItems.stream()
                .filter(item -> item.getProduct() != null && item.getUnitPrice() != null)
                .map(item -> new CartResponse.Item(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))))
                .toList();
        BigDecimal total = items.stream()
                .map(CartResponse.Item::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(
                cart.getId(),
                cart.getClient() != null ? cart.getClient().getId() : null,
                items,
                total);
    }

    public SalesOrderResponse toOrderResponse(SalesOrderJpaEntity order) {
        var lines = order.getLines().stream()
                .map(line -> new SalesOrderResponse.Line(
                        line.getProduct().getId(),
                        line.getQuantity(),
                        line.getUnitPrice()))
                .toList();
        return new SalesOrderResponse(
                order.getId(),
                ApiDisplayFormatter.orderCode(order.getId()),
                order.getClient().getId(),
                order.getClient().getName(),
                order.getWarehouse() == null ? null : order.getWarehouse().getId(),
                order.getWarehouse() == null ? null : order.getWarehouse().getCode(),
                order.getStatus(),
                order.getTotal(),
                order.getCreatedAt(),
                ApiDisplayFormatter.formatDateTime(order.getCreatedAt()),
                lines);
    }

    public SalesOrderListItem toListItem(SalesOrderJpaEntity order) {
        return new SalesOrderListItem(
                order.getId(),
                ApiDisplayFormatter.orderCode(order.getId()),
                order.getClient().getId(),
                order.getClient().getName(),
                order.getStatus(),
                order.getTotal(),
                order.getCreatedAt(),
                ApiDisplayFormatter.formatDateTime(order.getCreatedAt()));
    }
}
