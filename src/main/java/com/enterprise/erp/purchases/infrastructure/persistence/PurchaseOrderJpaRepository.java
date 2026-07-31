package com.enterprise.erp.purchases.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface PurchaseOrderJpaRepository extends JpaRepository<PurchaseOrderJpaEntity, Long> {

    Page<PurchaseOrderJpaEntity> findByStatus(String status, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM PurchaseOrderJpaEntity o WHERE o.id = :id")
    Optional<PurchaseOrderJpaEntity> findByIdForUpdate(@Param("id") Long id);
}
