package com.empresa.erp.compras.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderResponse(
        Long id,
        String orderCode,
        Long supplierId,
        String supplierName,
        String status,
        BigDecimal total,
        LocalDateTime createdAt,
        String createdAtFormatted,
        List<Line> lines
) {
    public record Line(
            Long productId,
            String productSku,
            int quantity,
            BigDecimal unitPrice
    ) {
    }
}
