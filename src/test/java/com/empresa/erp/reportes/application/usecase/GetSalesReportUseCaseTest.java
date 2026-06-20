package com.empresa.erp.reportes.application.usecase;

import com.empresa.erp.reportes.application.dto.SalesReportResponse;
import com.empresa.erp.reportes.application.dto.SalesDailyTotalProjection;
import com.empresa.erp.ventas.infrastructure.persistence.SalesOrderJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSalesReportUseCaseTest {

    @Mock
    private SalesOrderJpaRepository salesOrderJpaRepository;

    @InjectMocks
    private GetSalesReportUseCase useCase;

    @Test
    void returnsDailyTotalsOrderedFromNewestToOldest() {
        LocalDate from = LocalDate.of(2026, 6, 1);
        LocalDate to = LocalDate.of(2026, 6, 3);

        when(salesOrderJpaRepository.summarizeDailySales(any(), any(), eq("COMPLETED")))
                .thenReturn(List.of(
                        dailyTotal(LocalDate.of(2026, 6, 1), 1, new BigDecimal("10.00")),
                        dailyTotal(LocalDate.of(2026, 6, 3), 1, new BigDecimal("25.00"))
                ));

        SalesReportResponse response = useCase.execute(from, to);

        assertThat(response.dailyTotals())
                .extracting(SalesReportResponse.DailyTotal::date)
                .containsExactly(
                        LocalDate.of(2026, 6, 3),
                        LocalDate.of(2026, 6, 2),
                        LocalDate.of(2026, 6, 1)
                );
        assertThat(response.dailyTotals().getFirst().orders()).isEqualTo(1);
        assertThat(response.dailyTotals().getFirst().total()).isEqualByComparingTo("25.00");
    }

    private SalesDailyTotalProjection dailyTotal(LocalDate date, long orders, BigDecimal total) {
        return new SalesDailyTotalProjection() {
            @Override
            public LocalDate getSaleDate() {
                return date;
            }

            @Override
            public long getOrders() {
                return orders;
            }

            @Override
            public BigDecimal getTotal() {
                return total;
            }
        };
    }
}
