package com.empresa.erp.ventas.infrastructure.persistence;

import com.empresa.erp.reportes.application.dto.SalesDailyTotalProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface SalesOrderJpaRepository extends JpaRepository<SalesOrderJpaEntity, Long> {

    Page<SalesOrderJpaEntity> findByClientId(Long clientId, Pageable pageable);

    long countByCreatedAtBetweenAndStatus(LocalDateTime from, LocalDateTime to, String status);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM SalesOrderJpaEntity o WHERE o.createdAt BETWEEN :from AND :to AND o.status = :status")
    BigDecimal sumTotalByCreatedAtBetweenAndStatus(@Param("from") LocalDateTime from,
                                                   @Param("to") LocalDateTime to,
                                                   @Param("status") String status);

    List<SalesOrderJpaEntity> findByCreatedAtBetweenAndStatus(LocalDateTime from, LocalDateTime to, String status);

    @Query(value = """
            SELECT CAST(o.created_at AS date) AS "saleDate",
                   COUNT(*) AS "orders",
                   COALESCE(SUM(o.total), 0) AS "total"
            FROM erp.sales_orders o
            WHERE o.created_at >= :from
              AND o.created_at < :to
              AND o.status = :status
            GROUP BY CAST(o.created_at AS date)
            ORDER BY CAST(o.created_at AS date) DESC
            """, nativeQuery = true)
    List<SalesDailyTotalProjection> summarizeDailySales(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("status") String status);

    @Query("""
            SELECT o FROM SalesOrderJpaEntity o
            JOIN o.client c
            WHERE (COALESCE(:status, '') = '' OR o.status = :status)
              AND o.createdAt >= COALESCE(:from, o.createdAt)
              AND o.createdAt <= COALESCE(:to, o.createdAt)
              AND (
                    COALESCE(:searchPattern, '') = '' OR
                    (:orderId IS NOT NULL AND o.id = :orderId) OR
                    LOWER(c.name) LIKE :searchPattern OR
                    CAST(o.id AS string) LIKE :searchPattern OR
                    LOWER(o.status) LIKE :searchPattern
                  )
            """)
    Page<SalesOrderJpaEntity> search(
            @Param("searchPattern") String searchPattern,
            @Param("orderId") Long orderId,
            @Param("status") String status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);
}
