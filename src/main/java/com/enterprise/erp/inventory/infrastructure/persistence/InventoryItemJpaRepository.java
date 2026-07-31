package com.enterprise.erp.inventory.infrastructure.persistence;

import com.enterprise.erp.reports.application.dto.InventoryStockProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface InventoryItemJpaRepository extends JpaRepository<InventoryItemJpaEntity, Long> {

    Optional<InventoryItemJpaEntity> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    @Query("""
            SELECT i FROM InventoryItemJpaEntity i
            JOIN i.product p
            JOIN i.warehouse w
            LEFT JOIN p.category c
            WHERE (:searchPattern IS NULL
                   OR LOWER(p.name) LIKE :searchPattern
                   OR LOWER(p.sku) LIKE :searchPattern
                   OR LOWER(w.name) LIKE :searchPattern
                   OR LOWER(w.code) LIKE :searchPattern)
              AND (:productId IS NULL OR p.id = :productId)
              AND (:warehouseId IS NULL OR w.id = :warehouseId)
              AND (:categoryId IS NULL OR c.id = :categoryId)
              AND (:lowStock IS NULL
                   OR (:lowStock = true AND i.quantity < p.minStock)
                   OR (:lowStock = false AND i.quantity >= p.minStock))
            """)
    Page<InventoryItemJpaEntity> search(
            @Param("searchPattern") String searchPattern,
            @Param("productId") Long productId,
            @Param("warehouseId") Long warehouseId,
            @Param("categoryId") Long categoryId,
            @Param("lowStock") Boolean lowStock,
            Pageable pageable);

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
