package com.enterprise.erp.roles.application.dto;

import java.util.Set;

public record RoleResponse(
        Long id,
        String name,
        String description,
        boolean active,
        Set<String> permissions
) {
}
