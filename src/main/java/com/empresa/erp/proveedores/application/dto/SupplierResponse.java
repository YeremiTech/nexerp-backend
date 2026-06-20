package com.empresa.erp.proveedores.application.dto;

public record SupplierResponse(
        Long id,
        String supplierCode,
        String name,
        String taxId,
        String email,
        String phone,
        boolean active
) {
}
