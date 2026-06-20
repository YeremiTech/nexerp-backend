package com.empresa.erp.ventas.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SalesCartJpaRepository extends JpaRepository<SalesCartJpaEntity, Long> {

    Optional<SalesCartJpaEntity> findFirstByUser_IdOrderByUpdatedAtDesc(Long userId);

    @Query("""
            SELECT DISTINCT c FROM SalesCartJpaEntity c
            LEFT JOIN FETCH c.items i
            LEFT JOIN FETCH i.product
            WHERE c.id = :id
            """)
    Optional<SalesCartJpaEntity> findByIdWithItems(@Param("id") Long id);

    default Optional<SalesCartJpaEntity> findLatestByUserIdWithItems(Long userId) {
        return findFirstByUser_IdOrderByUpdatedAtDesc(userId)
                .flatMap(cart -> findByIdWithItems(cart.getId()));
    }
}
