package com.empresa.erp.reportes.application.usecase;

import com.empresa.erp.reportes.application.dto.SalesReportResponse;
import com.empresa.erp.shared.util.ApiDisplayFormatter;
import com.empresa.erp.ventas.infrastructure.persistence.SalesOrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GetSalesReportUseCase {

    private final SalesOrderJpaRepository salesOrderJpaRepository;

    @Transactional(readOnly = true)
    public SalesReportResponse execute(LocalDate from, LocalDate to) {
        var summaries = salesOrderJpaRepository.summarizeDailySales(
                from.atStartOfDay(),
                to.plusDays(1).atStartOfDay(),
                "COMPLETED");

        BigDecimal total = summaries.stream()
                .map(summary -> summary.getTotal() != null ? summary.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long orderCount = summaries.stream()
                .mapToLong(summary -> summary.getOrders())
                .sum();

        Map<LocalDate, SalesReportResponse.DailyTotal> totalsByDate = new HashMap<>();
        for (var summary : summaries) {
            LocalDate date = summary.getSaleDate();
            totalsByDate.put(date, new SalesReportResponse.DailyTotal(
                    date,
                    ApiDisplayFormatter.formatDate(date),
                    summary.getOrders(),
                    summary.getTotal() != null ? summary.getTotal() : BigDecimal.ZERO));
        }

        List<SalesReportResponse.DailyTotal> dailyTotals = new ArrayList<>();
        for (LocalDate date = to; !date.isBefore(from); date = date.minusDays(1)) {
            dailyTotals.add(totalsByDate.getOrDefault(date, new SalesReportResponse.DailyTotal(
                    date,
                    ApiDisplayFormatter.formatDate(date),
                    0,
                    BigDecimal.ZERO)));
        }

        return new SalesReportResponse(
                from,
                ApiDisplayFormatter.formatDate(from),
                to,
                ApiDisplayFormatter.formatDate(to),
                orderCount,
                total,
                dailyTotals);
    }
}
