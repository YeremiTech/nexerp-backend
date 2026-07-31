package com.enterprise.erp.inventory.application.dto;

import com.enterprise.erp.inventory.domain.InventoryMovementType;
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
