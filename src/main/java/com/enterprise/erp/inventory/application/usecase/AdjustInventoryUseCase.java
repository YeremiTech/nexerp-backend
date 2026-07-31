package com.enterprise.erp.inventory.application.usecase;

import com.enterprise.erp.inventory.application.dto.InventoryAdjustRequest;
import com.enterprise.erp.inventory.application.dto.InventoryMovementResponse;
import com.enterprise.erp.inventory.application.mapper.InventoryMapper;
import com.enterprise.erp.inventory.application.service.InventoryStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdjustInventoryUseCase {

    private final InventoryStockService inventoryStockService;
    private final InventoryMapper inventoryMapper;

    @Transactional
    public InventoryMovementResponse execute(InventoryAdjustRequest request) {
        var movement = inventoryStockService.adjustToQuantity(
                request.productId(), request.warehouseId(), request.newQuantity(), "ADJUSTMENT", null);
        return inventoryMapper.toResponse(movement);
    }
}
