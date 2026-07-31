package com.enterprise.erp.sales.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalesOrderListItem(
        Long id,
        String orderCode,
        Long clientId,
        String clientName,
        String status,
        BigDecimal total,
        LocalDateTime createdAt,
        String createdAtFormatted
) {
}
