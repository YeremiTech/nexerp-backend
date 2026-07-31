package com.enterprise.erp.categories.application.dto;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @Size(max = 100) String name,
        Long parentId,
        Boolean active
) {
}
