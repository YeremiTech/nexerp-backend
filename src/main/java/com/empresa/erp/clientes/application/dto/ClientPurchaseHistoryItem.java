package com.empresa.erp.clientes.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClientPurchaseHistoryItem(
        Long orderId,
        String orderCode,
        LocalDateTime createdAt,
        String createdAtFormatted,
        String status,
        BigDecimal total
) {
}
