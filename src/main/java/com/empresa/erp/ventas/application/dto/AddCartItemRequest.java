package com.empresa.erp.ventas.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record AddCartItemRequest(
        @NotNull Long productId,
        @NotNull @Positive int quantity
) {
}
