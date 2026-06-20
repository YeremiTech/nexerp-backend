package com.empresa.erp.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SessionActivityRequest(
        @NotBlank String refreshToken,
        @NotNull Boolean active
) {
}
