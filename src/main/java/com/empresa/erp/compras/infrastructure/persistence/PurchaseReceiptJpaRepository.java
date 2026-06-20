package com.empresa.erp.compras.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseReceiptJpaRepository extends JpaRepository<PurchaseReceiptJpaEntity, Long> {
}
