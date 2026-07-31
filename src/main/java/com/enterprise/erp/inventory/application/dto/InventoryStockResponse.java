package com.enterprise.erp.inventory.application.dto;

public record InventoryStockResponse(
        Long id,
        Long productId,
        String productSku,
        String productName,
        Long categoryId,
        Long warehouseId,
        String warehouseCode,
        String warehouseName,
        int quantity,
        int minStock,
        boolean lowStock
) {
}
