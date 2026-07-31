package com.enterprise.erp.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordResetConfirmDto(
        @NotBlank @Pattern(regexp = "\\d{4}", message = "El código debe tener 4 dígitos") String token,
        @NotBlank @Size(min = 8, max = 100) String newPassword
) {
}
