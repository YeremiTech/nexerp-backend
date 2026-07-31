package com.enterprise.erp.sales.application.usecase;

import com.enterprise.erp.inventory.application.dto.InventoryMovementRequest;
import com.enterprise.erp.inventory.application.usecase.EntryInventoryUseCase;
import com.enterprise.erp.sales.application.dto.SalesOrderResponse;
import com.enterprise.erp.sales.application.mapper.SalesMapper;
import com.enterprise.erp.sales.infrastructure.persistence.SalesOrderJpaEntity;
import com.enterprise.erp.sales.infrastructure.persistence.SalesOrderJpaRepository;
import com.enterprise.erp.shared.application.constants.ApiErrorCode;
import com.enterprise.erp.shared.domain.exception.BusinessRuleException;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelSalesOrderUseCase {

    private final SalesOrderJpaRepository salesOrderJpaRepository;
    private final EntryInventoryUseCase entryInventoryUseCase;
    private final SalesMapper salesMapper;

    @Transactional
    public SalesOrderResponse execute(Long id) {
        SalesOrderJpaEntity order = salesOrderJpaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de venta", id));
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new BusinessRuleException(ApiErrorCode.SALES_ORDER_INVALID_CANCEL);
        }
        if (order.getWarehouse() == null) {
            throw new BusinessRuleException(ApiErrorCode.SALES_ORDER_WAREHOUSE_REQUIRED);
        }

        for (var line : order.getLines()) {
            entryInventoryUseCase.execute(new InventoryMovementRequest(
                    line.getProduct().getId(),
                    order.getWarehouse().getId(),
                    line.getQuantity(),
                    "SALES_ORDER_CANCELLATION",
                    order.getId()));
        }
        order.setStatus("CANCELLED");
        return salesMapper.toOrderResponse(salesOrderJpaRepository.save(order));
    }
}
