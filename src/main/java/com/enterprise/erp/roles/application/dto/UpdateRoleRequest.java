package com.enterprise.erp.roles.application.dto;

import jakarta.validation.constraints.Size;

public record UpdateRoleRequest(
        @Size(max = 50) String name,
        @Size(max = 255) String description,
        Boolean active
) {
}
