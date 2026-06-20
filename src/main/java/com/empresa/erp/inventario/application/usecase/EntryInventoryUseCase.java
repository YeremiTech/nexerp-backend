package com.empresa.erp.inventario.application.usecase;

import com.empresa.erp.inventario.application.dto.InventoryMovementRequest;
import com.empresa.erp.inventario.application.dto.InventoryMovementResponse;
import com.empresa.erp.inventario.application.mapper.InventoryMapper;
import com.empresa.erp.inventario.application.service.InventoryStockService;
import com.empresa.erp.inventario.domain.InventoryMovementType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EntryInventoryUseCase {

    private final InventoryStockService inventoryStockService;
    private final InventoryMapper inventoryMapper;

    @Transactional
    public InventoryMovementResponse execute(InventoryMovementRequest request) {
        var movement = inventoryStockService.applyMovement(
                request.productId(), request.warehouseId(), request.quantity(),
                InventoryMovementType.ENTRY, request.referenceType(), request.referenceId());
        return inventoryMapper.toResponse(movement);
    }
}
