package com.enterprise.erp.inventory.application.usecase;

import com.enterprise.erp.inventory.application.dto.InventoryMovementResponse;
import com.enterprise.erp.inventory.application.dto.InventoryTransferRequest;
import com.enterprise.erp.inventory.application.mapper.InventoryMapper;
import com.enterprise.erp.inventory.application.service.InventoryStockService;
import com.enterprise.erp.inventory.domain.InventoryMovementType;
import com.enterprise.erp.shared.application.constants.ApiErrorCode;
import com.enterprise.erp.shared.domain.exception.BusinessRuleException;
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
