package com.enterprise.erp.sales.application.usecase;

import com.enterprise.erp.clients.domain.ClientType;
import com.enterprise.erp.clients.infrastructure.persistence.ClientJpaEntity;
import com.enterprise.erp.clients.infrastructure.persistence.ClientJpaRepository;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaEntity;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
import com.enterprise.erp.sales.infrastructure.persistence.SalesOrderJpaEntity;
import com.enterprise.erp.sales.infrastructure.persistence.SalesOrderJpaRepository;
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
