package com.enterprise.erp.inventory.infrastructure.persistence;

import com.enterprise.erp.inventory.domain.InventoryMovementType;
import com.enterprise.erp.products.infrastructure.persistence.ProductJpaEntity;
import com.enterprise.erp.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_movements", schema = "erp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryMovementJpaEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductJpaEntity product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private WarehouseJpaEntity warehouse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InventoryMovementType type;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "reference_type", length = 50)
    private String referenceType;

    @Column(name = "reference_id")
    private Long referenceId;
}
