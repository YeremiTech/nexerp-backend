package com.empresa.erp.compras.application.dto;

import jakarta.validation.constraints.NotNull;

public record ReceivePurchaseOrderRequest(
        @NotNull Long warehouseId
) {
}
