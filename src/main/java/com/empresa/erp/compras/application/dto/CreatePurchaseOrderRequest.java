package com.empresa.erp.compras.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

public record CreatePurchaseOrderRequest(
        @NotNull Long supplierId,
        @NotNull List<Line> lines
) {
    public record Line(
            @NotNull Long productId,
            @Positive int quantity,
            @NotNull @PositiveOrZero BigDecimal unitPrice
    ) {
    }
}
