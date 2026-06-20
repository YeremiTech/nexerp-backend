package com.empresa.erp.productos.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        Long categoryId,
        String categoryName,
        int minStock,
        boolean active,
        BigDecimal currentPrice,
        String currency,
        List<String> imageUrls
) {
}
