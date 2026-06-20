package com.empresa.erp.inventario.application.dto;

import com.empresa.erp.inventario.domain.InventoryMovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InventoryMovementRequest(
        @NotNull Long productId,
        @NotNull Long warehouseId,
        @NotNull @Positive int quantity,
        String referenceType,
        Long referenceId
) {
}
