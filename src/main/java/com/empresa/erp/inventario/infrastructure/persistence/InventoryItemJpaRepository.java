package com.empresa.erp.inventario.infrastructure.persistence;

import com.empresa.erp.reportes.application.dto.InventoryStockProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface InventoryItemJpaRepository extends JpaRepository<InventoryItemJpaEntity, Long> {

    Optional<InventoryItemJpaEntity> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT i FROM InventoryItemJpaEntity i
            WHERE i.product.id = :productId AND i.warehouse.id = :warehouseId
            """)
    Optional<InventoryItemJpaEntity> findByProductIdAndWarehouseIdForUpdate(
            @Param("productId") Long productId,
            @Param("warehouseId") Long warehouseId);

    @Query("SELECT COUNT(i) FROM InventoryItemJpaEntity i WHERE i.quantity < i.product.minStock")
    long countLowStockItems();

    @Query("SELECT i FROM InventoryItemJpaEntity i JOIN FETCH i.product JOIN FETCH i.warehouse")
    List<InventoryItemJpaEntity> findAllWithDetails();

    @Query(value = """
            SELECT p.id AS "productId",
                   p.sku AS "productSku",
                   p.name AS "productName",
                   w.id AS "warehouseId",
                   w.code AS "warehouseCode",
                   i.quantity AS "quantity",
                   p.min_stock AS "minStock"
            FROM erp.inventory_items i
            JOIN erp.products p ON p.id = i.product_id
            JOIN erp.warehouses w ON w.id = i.warehouse_id
            ORDER BY p.name ASC, w.code ASC
            """, nativeQuery = true)
    List<InventoryStockProjection> findStockReport();
}
