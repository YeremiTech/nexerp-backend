package com.empresa.erp.compras.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseOrderListItem(
        Long id,
        String orderCode,
        Long supplierId,
        String supplierName,
        String status,
        BigDecimal total,
        LocalDateTime createdAt,
        String createdAtFormatted
) {
}
