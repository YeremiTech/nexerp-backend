package com.empresa.erp.clientes.infrastructure.persistence;

import com.empresa.erp.clientes.domain.ClientType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientJpaRepository extends JpaRepository<ClientJpaEntity, Long> {

    Page<ClientJpaEntity> findByActiveTrueOrderByNameAsc(Pageable pageable);

    long countByActiveTrue();

    @Query("""
            SELECT c FROM ClientJpaEntity c
            WHERE (:active IS NULL OR c.active = :active)
              AND (:type IS NULL OR c.type = :type)
              AND (
                    :searchPattern IS NULL OR
                    LOWER(c.name) LIKE :searchPattern OR
                    LOWER(COALESCE(c.taxId, '')) LIKE :searchPattern OR
                    LOWER(COALESCE(c.email, '')) LIKE :searchPattern OR
                    LOWER(COALESCE(c.phone, '')) LIKE :searchPattern OR
                    CAST(c.id AS string) LIKE :searchPattern
                  )
            """)
    Page<ClientJpaEntity> search(
            @Param("searchPattern") String searchPattern,
            @Param("type") ClientType type,
            @Param("active") Boolean active,
            Pageable pageable);
}
