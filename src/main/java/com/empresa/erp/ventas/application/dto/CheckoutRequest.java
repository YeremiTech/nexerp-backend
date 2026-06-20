package com.empresa.erp.ventas.application.dto;

import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotNull Long clientId,
        @NotNull Long warehouseId
) {
}
