package com.empresa.erp.inventario.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InventoryTransferRequest(
        @NotNull Long productId,
        @NotNull Long fromWarehouseId,
        @NotNull Long toWarehouseId,
        @NotNull @Positive int quantity
) {
}
