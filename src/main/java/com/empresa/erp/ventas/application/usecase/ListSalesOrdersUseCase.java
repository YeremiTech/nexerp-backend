package com.empresa.erp.ventas.application.usecase;

import com.empresa.erp.shared.util.ListSearchSupport;
import com.empresa.erp.shared.util.PageableSupport;
import com.empresa.erp.ventas.application.dto.SalesOrderListItem;
import com.empresa.erp.ventas.application.mapper.SalesMapper;
import com.empresa.erp.ventas.infrastructure.persistence.SalesOrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class ListSalesOrdersUseCase {

    private final SalesOrderJpaRepository salesOrderJpaRepository;
    private final SalesMapper salesMapper;

    @Transactional(readOnly = true)
    public Page<SalesOrderListItem> execute(String search, String status, LocalDate from, LocalDate to, Pageable pageable) {
        LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = to != null ? to.atTime(LocalTime.MAX) : null;
        String statusFilter = (status == null || status.isBlank()) ? null : status;

        pageable = PageableSupport.newestFirst(pageable);
        return salesOrderJpaRepository.search(
                ListSearchSupport.toLikePattern(search),
                ListSearchSupport.extractNumericIdFromSearch(search),
                statusFilter,
                fromDateTime,
                toDateTime,
                pageable
        ).map(salesMapper::toListItem);
    }
}
