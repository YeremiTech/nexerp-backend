package com.empresa.erp.compras.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderJpaRepository extends JpaRepository<PurchaseOrderJpaEntity, Long> {

    Page<PurchaseOrderJpaEntity> findByStatus(String status, Pageable pageable);
}
