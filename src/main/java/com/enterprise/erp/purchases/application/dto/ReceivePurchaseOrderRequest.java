package com.enterprise.erp.purchases.application.dto;

import jakarta.validation.constraints.NotNull;

public record ReceivePurchaseOrderRequest(
        @NotNull Long warehouseId
) {
}
