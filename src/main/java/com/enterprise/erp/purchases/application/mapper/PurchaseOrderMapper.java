package com.enterprise.erp.purchases.application.mapper;

import com.enterprise.erp.purchases.application.dto.PurchaseOrderListItem;
import com.enterprise.erp.purchases.application.dto.PurchaseOrderResponse;
import com.enterprise.erp.purchases.infrastructure.persistence.PurchaseOrderJpaEntity;
import com.enterprise.erp.shared.util.ApiDisplayFormatter;
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
