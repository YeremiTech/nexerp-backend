package com.empresa.erp.reportes.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SalesReportResponse(
        LocalDate from,
        String fromFormatted,
        LocalDate to,
        String toFormatted,
        long orderCount,
        BigDecimal totalAmount,
        List<DailyTotal> dailyTotals
) {
    public record DailyTotal(LocalDate date, String dateFormatted, long orders, BigDecimal total) {
    }
}
