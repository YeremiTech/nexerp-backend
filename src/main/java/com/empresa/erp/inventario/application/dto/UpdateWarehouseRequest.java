package com.empresa.erp.inventario.application.dto;

import jakarta.validation.constraints.Size;

public record UpdateWarehouseRequest(
        @Size(max = 100) String name,
        Boolean active
) {
}
