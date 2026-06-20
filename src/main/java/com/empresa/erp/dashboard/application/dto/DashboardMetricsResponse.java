package com.empresa.erp.dashboard.application.dto;

import java.math.BigDecimal;

public record DashboardMetricsResponse(
        long totalProducts,
        long totalClients,
        long totalSuppliers,
        long salesOrdersToday,
        BigDecimal salesTotalToday,
        long lowStockProducts
) {
}
