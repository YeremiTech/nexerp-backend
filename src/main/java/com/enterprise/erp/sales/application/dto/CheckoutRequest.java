package com.enterprise.erp.sales.application.dto;

import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotNull Long clientId,
        @NotNull Long warehouseId
) {
}
