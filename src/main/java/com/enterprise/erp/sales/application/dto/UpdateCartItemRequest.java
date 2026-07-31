package com.enterprise.erp.sales.application.dto;

import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequest(
        @Positive int quantity
) {
}
