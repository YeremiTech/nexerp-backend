package com.empresa.erp.inventario.application.usecase;

import com.empresa.erp.inventario.application.dto.InventoryMovementResponse;
import com.empresa.erp.inventario.application.dto.InventoryTransferRequest;
import com.empresa.erp.inventario.application.mapper.InventoryMapper;
import com.empresa.erp.inventario.application.service.InventoryStockService;
import com.empresa.erp.inventario.domain.InventoryMovementType;
import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferInventoryUseCase {

    private final InventoryStockService inventoryStockService;
    private final InventoryMapper inventoryMapper;

    @Transactional
    public InventoryMovementResponse execute(InventoryTransferRequest request) {
        if (request.fromWarehouseId().equals(request.toWarehouseId())) {
            throw new BusinessRuleException(ApiErrorCode.WAREHOUSE_SAME_TRANSFER);
        }
        inventoryStockService.applyMovement(
                request.productId(), request.fromWarehouseId(), -request.quantity(),
                InventoryMovementType.TRANSFER_OUT, "TRANSFER", null);
        var inMovement = inventoryStockService.applyMovement(
                request.productId(), request.toWarehouseId(), request.quantity(),
                InventoryMovementType.TRANSFER_IN, "TRANSFER", null);
        return inventoryMapper.toResponse(inMovement);
    }
}
