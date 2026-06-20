package com.empresa.erp.inventario.application.service;

import com.empresa.erp.infrastructure.config.RedisConfig;
import com.empresa.erp.inventario.domain.InventoryMovementType;
import com.empresa.erp.inventario.infrastructure.persistence.*;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaEntity;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaRepository;
import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import com.empresa.erp.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InventoryStockService {

    private final InventoryItemJpaRepository inventoryItemJpaRepository;
    private final InventoryMovementJpaRepository inventoryMovementJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final WarehouseJpaRepository warehouseJpaRepository;

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_DASHBOARD, allEntries = true)
    public InventoryMovementJpaEntity applyMovement(Long productId, Long warehouseId, int quantityDelta,
                                                    InventoryMovementType type, String referenceType, Long referenceId) {
        return applyMovementInternal(productId, warehouseId, quantityDelta, type, referenceType, referenceId);
    }

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_DASHBOARD, allEntries = true)
    public InventoryMovementJpaEntity adjustToQuantity(Long productId, Long warehouseId, int newQuantity,
                                                       String referenceType, Long referenceId) {
        InventoryItemJpaEntity item = findOrCreateLockedItem(productId, warehouseId);
        int quantityDelta = newQuantity - item.getQuantity();
        return updateItemAndRecordMovement(
                item,
                quantityDelta,
                InventoryMovementType.ADJUSTMENT,
                referenceType,
                referenceId
        );
    }

    private InventoryMovementJpaEntity applyMovementInternal(Long productId, Long warehouseId, int quantityDelta,
                                                            InventoryMovementType type, String referenceType, Long referenceId) {
        InventoryItemJpaEntity item = findOrCreateLockedItem(productId, warehouseId);
        return updateItemAndRecordMovement(item, quantityDelta, type, referenceType, referenceId);
    }

    private InventoryItemJpaEntity findOrCreateLockedItem(Long productId, Long warehouseId) {
        ProductJpaEntity product = productJpaRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));
        WarehouseJpaEntity warehouse = warehouseJpaRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Almacén", warehouseId));

        return inventoryItemJpaRepository
                .findByProductIdAndWarehouseIdForUpdate(productId, warehouseId)
                .orElseGet(() -> InventoryItemJpaEntity.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .quantity(0)
                        .build());
    }

    private InventoryMovementJpaEntity updateItemAndRecordMovement(InventoryItemJpaEntity item, int quantityDelta,
                                                                   InventoryMovementType type, String referenceType,
                                                                   Long referenceId) {
        int newQuantity = item.getQuantity() + quantityDelta;
        if (newQuantity < 0) {
            throw new BusinessRuleException(ApiErrorCode.INSUFFICIENT_STOCK);
        }
        item.setQuantity(newQuantity);
        inventoryItemJpaRepository.save(item);

        InventoryMovementJpaEntity movement = InventoryMovementJpaEntity.builder()
                .product(item.getProduct())
                .warehouse(item.getWarehouse())
                .type(type)
                .quantity(Math.abs(quantityDelta))
                .referenceType(referenceType)
                .referenceId(referenceId)
                .build();
        return inventoryMovementJpaRepository.save(movement);
    }
}
