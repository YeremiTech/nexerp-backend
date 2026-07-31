package com.enterprise.erp.purchases.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseReceiptJpaRepository extends JpaRepository<PurchaseReceiptJpaEntity, Long> {

    List<PurchaseReceiptJpaEntity> findByOrderIdOrderByReceivedAtDesc(Long orderId);
}
