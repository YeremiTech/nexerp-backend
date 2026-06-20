package com.empresa.erp.ventas.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SalesOrderResponse(
        Long id,
        String orderCode,
        Long clientId,
        String clientName,
        String status,
        BigDecimal total,
        LocalDateTime createdAt,
        String createdAtFormatted,
        List<Line> lines
) {
    public record Line(
            Long productId,
            int quantity,
            BigDecimal unitPrice
    ) {
    }
}
