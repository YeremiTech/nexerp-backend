package com.empresa.erp.ventas.application.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long id,
        Long clientId,
        List<Item> items,
        BigDecimal total
) {
    public record Item(
            Long productId,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal lineTotal
    ) {
    }
}
