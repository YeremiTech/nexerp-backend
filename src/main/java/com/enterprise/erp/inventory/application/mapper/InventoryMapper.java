package com.enterprise.erp.inventory.application.mapper;

import com.enterprise.erp.inventory.application.dto.InventoryMovementResponse;
import com.enterprise.erp.inventory.infrastructure.persistence.InventoryMovementJpaEntity;
import com.enterprise.erp.shared.util.ApiDisplayFormatter;
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
