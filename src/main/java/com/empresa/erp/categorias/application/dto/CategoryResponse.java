package com.empresa.erp.categorias.application.dto;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        Long parentId,
        boolean active,
        LocalDateTime createdAt,
        String createdAtFormatted
) {
}
