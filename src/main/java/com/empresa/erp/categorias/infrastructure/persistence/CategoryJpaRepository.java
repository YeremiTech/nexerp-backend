package com.empresa.erp.categorias.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, Long> {

    List<CategoryJpaEntity> findByActiveTrue();

    Page<CategoryJpaEntity> findByActiveTrue(Pageable pageable);

    @Query("""
            SELECT c FROM CategoryJpaEntity c
            WHERE (:active IS NULL OR c.active = :active)
            """)
    Page<CategoryJpaEntity> search(@Param("active") Boolean active, Pageable pageable);
}
