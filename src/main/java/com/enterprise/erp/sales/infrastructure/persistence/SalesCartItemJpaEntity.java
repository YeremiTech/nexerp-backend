package com.enterprise.erp.sales.infrastructure.persistence;

import com.enterprise.erp.products.infrastructure.persistence.ProductJpaEntity;
import com.enterprise.erp.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "sales_cart_items", schema = "erp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesCartItemJpaEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private SalesCartJpaEntity cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductJpaEntity product;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;
}
