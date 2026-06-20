package com.empresa.erp.ventas.infrastructure.persistence;

import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaEntity;
import com.empresa.erp.shared.domain.AuditableEntity;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "sales_carts",
        schema = "erp",
        uniqueConstraints = @UniqueConstraint(name = "uk_sales_carts_user_id", columnNames = "user_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesCartJpaEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientJpaEntity client;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SalesCartItemJpaEntity> items = new ArrayList<>();
}
