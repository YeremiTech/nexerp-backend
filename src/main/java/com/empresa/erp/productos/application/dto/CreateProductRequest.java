package com.empresa.erp.productos.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequest(
        @NotBlank @Size(max = 200) String name,
        String description,
        Long categoryId,
        @PositiveOrZero int minStock,
        @NotNull @PositiveOrZero BigDecimal price,
        @Size(max = 3) String currency,
        List<String> imageUrls
) {
}
