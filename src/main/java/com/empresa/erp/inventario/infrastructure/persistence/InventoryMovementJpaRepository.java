package com.empresa.erp.inventario.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementJpaRepository extends JpaRepository<InventoryMovementJpaEntity, Long> {

    Page<InventoryMovementJpaEntity> findByProductId(Long productId, Pageable pageable);

    Page<InventoryMovementJpaEntity> findByProductIdAndWarehouseId(
            Long productId, Long warehouseId, Pageable pageable);
}
