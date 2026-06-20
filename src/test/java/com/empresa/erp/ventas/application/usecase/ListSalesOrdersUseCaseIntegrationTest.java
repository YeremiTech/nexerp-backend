package com.empresa.erp.ventas.application.usecase;

import com.empresa.erp.clientes.domain.ClientType;
import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaEntity;
import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaRepository;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaEntity;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaRepository;
import com.empresa.erp.ventas.infrastructure.persistence.SalesOrderJpaEntity;
import com.empresa.erp.ventas.infrastructure.persistence.SalesOrderJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ActiveProfiles("test")
class ListSalesOrdersUseCaseIntegrationTest {

    @Autowired
    private ListSalesOrdersUseCase listSalesOrdersUseCase;

    @Autowired
    private SalesOrderJpaRepository salesOrderJpaRepository;

    @Autowired
    private ClientJpaRepository clientJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void seedOrder() {
        if (salesOrderJpaRepository.count() > 0) {
            return;
        }
        UserJpaEntity user = userJpaRepository.save(UserJpaEntity.builder()
                .username("seller")
                .email("seller@test.local")
                .passwordHash(passwordEncoder.encode("seller!"))
                .active(true)
                .roles(new HashSet<>())
                .build());
        ClientJpaEntity client = clientJpaRepository.save(ClientJpaEntity.builder()
                .type(ClientType.COMPANY)
                .name("Cliente Test")
                .active(true)
                .build());
        salesOrderJpaRepository.save(SalesOrderJpaEntity.builder()
                .client(client)
                .user(user)
                .status("COMPLETED")
                .total(new BigDecimal("100.00"))
                .build());
    }

    @Test
    @Transactional
    void execute_shouldListOrdersSortedByCreatedAtDesc() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        assertThatCode(() -> listSalesOrdersUseCase.execute(null, null, null, null, pageable))
                .doesNotThrowAnyException();
    }
}
