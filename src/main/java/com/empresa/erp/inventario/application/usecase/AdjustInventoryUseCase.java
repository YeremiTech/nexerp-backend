package com.empresa.erp.inventario.application.usecase;

import com.empresa.erp.inventario.application.dto.InventoryAdjustRequest;
import com.empresa.erp.inventario.application.dto.InventoryMovementResponse;
import com.empresa.erp.inventario.application.mapper.InventoryMapper;
import com.empresa.erp.inventario.application.service.InventoryStockService;
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
