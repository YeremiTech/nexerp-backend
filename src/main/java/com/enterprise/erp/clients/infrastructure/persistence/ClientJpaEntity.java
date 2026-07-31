package com.enterprise.erp.clients.infrastructure.persistence;

import com.enterprise.erp.clients.domain.ClientType;
import com.enterprise.erp.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clients", schema = "erp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientJpaEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = ClientTypeConverter.class)
    @Column(nullable = false, length = 20)
    private ClientType type;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(nullable = false)
    private boolean active;
}
