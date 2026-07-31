package com.enterprise.erp.inventory.infrastructure.persistence;

import com.enterprise.erp.products.infrastructure.persistence.ProductJpaEntity;
import com.enterprise.erp.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "inventory_items",
        schema = "erp",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_inventory_items_product_warehouse",
                columnNames = {"product_id", "warehouse_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItemJpaEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductJpaEntity product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private WarehouseJpaEntity warehouse;

    @Column(nullable = false)
    private int quantity;

    @Version
    @Column(nullable = false)
    private Long version;
}
