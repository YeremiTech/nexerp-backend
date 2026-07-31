package com.enterprise.erp.sales.application.usecase;

import com.enterprise.erp.sales.application.dto.SalesOrderResponse;
import com.enterprise.erp.sales.application.mapper.SalesMapper;
import com.enterprise.erp.sales.infrastructure.persistence.SalesOrderJpaRepository;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetSalesOrderUseCase {

    private final SalesOrderJpaRepository salesOrderJpaRepository;
    private final SalesMapper salesMapper;

    @Transactional(readOnly = true)
    public SalesOrderResponse execute(Long id) {
        return salesOrderJpaRepository.findDetailedById(id)
                .map(salesMapper::toOrderResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de venta", id));
    }
}
