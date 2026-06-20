package com.empresa.erp.categorias.application.dto;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @Size(max = 100) String name,
        Long parentId,
        Boolean active
) {
}
