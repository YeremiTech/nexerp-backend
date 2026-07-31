package com.enterprise.erp.inventory.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWarehouseRequest(
        @NotBlank @Size(max = 100) String name
) {
}
