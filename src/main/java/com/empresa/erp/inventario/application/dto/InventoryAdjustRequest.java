package com.empresa.erp.inventario.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record InventoryAdjustRequest(
        @NotNull Long productId,
        @NotNull Long warehouseId,
        @NotNull @PositiveOrZero int newQuantity
) {
}
