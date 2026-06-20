package com.empresa.erp.roles.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoleRequest(
        @NotBlank @Size(max = 50) String name,
        @Size(max = 255) String description
) {
}
