package com.empresa.erp.compras.application.mapper;

import com.empresa.erp.compras.application.dto.PurchaseOrderListItem;
import com.empresa.erp.compras.application.dto.PurchaseOrderResponse;
import com.empresa.erp.compras.infrastructure.persistence.PurchaseOrderJpaEntity;
import com.empresa.erp.shared.util.ApiDisplayFormatter;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderMapper {

    public PurchaseOrderResponse toResponse(PurchaseOrderJpaEntity entity) {
        var lines = entity.getLines().stream()
                .map(line -> new PurchaseOrderResponse.Line(
                        line.getProduct().getId(),
                        line.getProduct().getSku(),
                        line.getQuantity(),
                        line.getUnitPrice()))
                .toList();
        return new PurchaseOrderResponse(
                entity.getId(),
                ApiDisplayFormatter.purchaseOrderCode(entity.getId()),
                entity.getSupplier().getId(),
                entity.getSupplier().getName(),
                entity.getStatus(),
                entity.getTotal(),
                entity.getCreatedAt(),
                ApiDisplayFormatter.formatDateTime(entity.getCreatedAt()),
                lines);
    }

    public PurchaseOrderListItem toListItem(PurchaseOrderJpaEntity entity) {
        return new PurchaseOrderListItem(
                entity.getId(),
                ApiDisplayFormatter.purchaseOrderCode(entity.getId()),
                entity.getSupplier().getId(),
                entity.getSupplier().getName(),
                entity.getStatus(),
                entity.getTotal(),
                entity.getCreatedAt(),
                ApiDisplayFormatter.formatDateTime(entity.getCreatedAt()));
    }
}
