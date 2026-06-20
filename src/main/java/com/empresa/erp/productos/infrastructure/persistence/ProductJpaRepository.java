package com.empresa.erp.productos.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    Optional<ProductJpaEntity> findBySku(String sku);

    @Query("SELECT p FROM ProductJpaEntity p LEFT JOIN FETCH p.prices WHERE p.id = :id")
    Optional<ProductJpaEntity> findByIdWithPrices(@Param("id") Long id);

    boolean existsBySku(String sku);

    Page<ProductJpaEntity> findByActiveTrueOrderByNameAsc(Pageable pageable);

    long countByActiveTrue();

    @Query("""
            SELECT p FROM ProductJpaEntity p
            LEFT JOIN p.category cat
            WHERE (:active IS NULL OR p.active = :active)
              AND (:categoryId IS NULL OR cat.id = :categoryId)
              AND (
                    :searchPattern IS NULL OR
                    LOWER(p.sku) LIKE :searchPattern OR
                    LOWER(p.name) LIKE :searchPattern OR
                    LOWER(COALESCE(p.description, '')) LIKE :searchPattern OR
                    LOWER(COALESCE(cat.name, '')) LIKE :searchPattern
                  )
            """)
    Page<ProductJpaEntity> search(
            @Param("searchPattern") String searchPattern,
            @Param("categoryId") Long categoryId,
            @Param("active") Boolean active,
            Pageable pageable);
}
