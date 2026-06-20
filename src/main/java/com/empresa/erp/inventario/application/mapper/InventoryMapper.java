package com.empresa.erp.inventario.application.mapper;

import com.empresa.erp.inventario.application.dto.InventoryMovementResponse;
import com.empresa.erp.inventario.infrastructure.persistence.InventoryMovementJpaEntity;
import com.empresa.erp.shared.util.ApiDisplayFormatter;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryMovementResponse toResponse(InventoryMovementJpaEntity entity) {
        return new InventoryMovementResponse(
                entity.getId(),
                ApiDisplayFormatter.movementCode(entity.getId()),
                entity.getProduct().getId(),
                entity.getProduct().getSku(),
                entity.getProduct().getName(),
                entity.getWarehouse().getId(),
                entity.getWarehouse().getCode(),
                entity.getType(),
                entity.getQuantity(),
                entity.getReferenceType(),
                entity.getReferenceId(),
                ApiDisplayFormatter.referenceLabel(entity.getReferenceType(), entity.getReferenceId()),
                entity.getCreatedAt(),
                ApiDisplayFormatter.formatDateTime(entity.getCreatedAt()));
    }
}
