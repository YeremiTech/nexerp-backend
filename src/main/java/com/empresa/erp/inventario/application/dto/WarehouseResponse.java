package com.empresa.erp.inventario.application.dto;

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
