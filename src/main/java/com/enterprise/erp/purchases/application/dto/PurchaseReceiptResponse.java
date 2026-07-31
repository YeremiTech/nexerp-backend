package com.enterprise.erp.purchases.application.dto;

import java.time.LocalDateTime;

public record PurchaseReceiptResponse(
        Long id,
        Long orderId,
        LocalDateTime receivedAt
) {
}
