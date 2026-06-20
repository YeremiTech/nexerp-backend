package com.empresa.erp.inventario.application.usecase;

import com.empresa.erp.inventario.application.dto.InventoryMovementResponse;
import com.empresa.erp.inventario.application.mapper.InventoryMapper;
import com.empresa.erp.inventario.infrastructure.persistence.InventoryMovementJpaRepository;
import com.empresa.erp.shared.util.PageableSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetKardexUseCase {

    private final InventoryMovementJpaRepository inventoryMovementJpaRepository;
    private final InventoryMapper inventoryMapper;

    @Transactional(readOnly = true)
    public Page<InventoryMovementResponse> execute(Long productId, Long warehouseId, Pageable pageable) {
        pageable = PageableSupport.newestFirst(pageable);
        Page<com.empresa.erp.inventario.infrastructure.persistence.InventoryMovementJpaEntity> page;
        if (warehouseId != null) {
            page = inventoryMovementJpaRepository
                    .findByProductIdAndWarehouseId(productId, warehouseId, pageable);
        } else {
            page = inventoryMovementJpaRepository.findByProductId(productId, pageable);
        }
        return page.map(inventoryMapper::toResponse);
    }
}
