package com.enterprise.erp.inventory.application.dto;

import java.time.LocalDateTime;

public record WarehouseResponse(
        Long id,
        String code,
        String name,
        boolean active,
        LocalDateTime createdAt,
        String createdAtFormatted
) {
}
