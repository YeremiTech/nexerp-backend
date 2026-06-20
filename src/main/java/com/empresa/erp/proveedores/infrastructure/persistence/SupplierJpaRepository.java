package com.empresa.erp.proveedores.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupplierJpaRepository extends JpaRepository<SupplierJpaEntity, Long> {

    Page<SupplierJpaEntity> findByActiveTrueOrderByNameAsc(Pageable pageable);

    long countByActiveTrue();

    @Query("""
            SELECT s FROM SupplierJpaEntity s
            WHERE (:active IS NULL OR s.active = :active)
              AND (:hasTaxId IS NULL OR (:hasTaxId = TRUE AND s.taxId IS NOT NULL AND s.taxId <> '') OR (:hasTaxId = FALSE AND (s.taxId IS NULL OR s.taxId = '')))
              AND (
                    :searchPattern IS NULL OR
                    LOWER(s.name) LIKE :searchPattern OR
                    LOWER(COALESCE(s.taxId, '')) LIKE :searchPattern OR
                    LOWER(COALESCE(s.email, '')) LIKE :searchPattern OR
                    LOWER(COALESCE(s.phone, '')) LIKE :searchPattern OR
                    CAST(s.id AS string) LIKE :searchPattern
                  )
            """)
    Page<SupplierJpaEntity> search(
            @Param("searchPattern") String searchPattern,
            @Param("hasTaxId") Boolean hasTaxId,
            @Param("active") Boolean active,
            Pageable pageable);
}
