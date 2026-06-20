package com.empresa.erp.inventario.application.dto;

import com.empresa.erp.inventario.domain.InventoryMovementType;

import java.time.LocalDateTime;

public record InventoryMovementResponse(
        Long id,
        String movementCode,
        Long productId,
        String productSku,
        String productName,
        Long warehouseId,
        String warehouseCode,
        InventoryMovementType type,
        int quantity,
        String referenceType,
        Long referenceId,
        String referenceLabel,
        LocalDateTime createdAt,
        String createdAtFormatted
) {
}
