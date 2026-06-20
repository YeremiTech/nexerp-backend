package com.empresa.erp.reportes.application.dto;

import java.util.List;

public record InventoryReportResponse(
        long totalItems,
        long totalUnits,
        List<StockItem> items
) {
    public record StockItem(
            Long productId,
            String productSku,
            String productName,
            Long warehouseId,
            String warehouseCode,
            int quantity,
            int minStock,
            boolean belowMinStock
    ) {
    }
}
