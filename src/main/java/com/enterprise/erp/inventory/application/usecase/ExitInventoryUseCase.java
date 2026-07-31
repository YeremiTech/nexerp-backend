package com.enterprise.erp.inventory.application.usecase;

import com.enterprise.erp.inventory.application.dto.InventoryMovementRequest;
import com.enterprise.erp.inventory.application.dto.InventoryMovementResponse;
import com.enterprise.erp.inventory.application.mapper.InventoryMapper;
import com.enterprise.erp.inventory.application.service.InventoryStockService;
import com.enterprise.erp.inventory.domain.InventoryMovementType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExitInventoryUseCase {

    private final InventoryStockService inventoryStockService;
    private final InventoryMapper inventoryMapper;

    @Transactional
    public InventoryMovementResponse execute(InventoryMovementRequest request) {
        var movement = inventoryStockService.applyMovement(
                request.productId(), request.warehouseId(), -request.quantity(),
                InventoryMovementType.EXIT, request.referenceType(), request.referenceId());
        return inventoryMapper.toResponse(movement);
    }
}
