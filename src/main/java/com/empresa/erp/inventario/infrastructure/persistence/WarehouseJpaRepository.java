package com.empresa.erp.inventario.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WarehouseJpaRepository extends JpaRepository<WarehouseJpaEntity, Long> {

    Optional<WarehouseJpaEntity> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    List<WarehouseJpaEntity> findByActiveTrueOrderByNameAsc();

    Page<WarehouseJpaEntity> findByActiveTrue(Pageable pageable);

    @Query("""
            SELECT w FROM WarehouseJpaEntity w
            WHERE (:active IS NULL OR w.active = :active)
            """)
    Page<WarehouseJpaEntity> search(@Param("active") Boolean active, Pageable pageable);
}
