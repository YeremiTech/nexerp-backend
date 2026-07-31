package com.enterprise.erp.sales.application.usecase;

import com.enterprise.erp.clients.domain.ClientType;
import com.enterprise.erp.clients.infrastructure.persistence.ClientJpaEntity;
import com.enterprise.erp.clients.infrastructure.persistence.ClientJpaRepository;
import com.enterprise.erp.inventory.application.dto.InventoryMovementRequest;
import com.enterprise.erp.inventory.application.dto.InventoryMovementResponse;
import com.enterprise.erp.inventory.application.usecase.ExitInventoryUseCase;
import com.enterprise.erp.inventory.infrastructure.persistence.WarehouseJpaEntity;
import com.enterprise.erp.inventory.infrastructure.persistence.WarehouseJpaRepository;
import com.enterprise.erp.products.infrastructure.persistence.ProductJpaEntity;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaEntity;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
import com.enterprise.erp.sales.application.dto.CheckoutRequest;
import com.enterprise.erp.sales.application.dto.SalesOrderResponse;
import com.enterprise.erp.sales.application.mapper.SalesMapper;
import com.enterprise.erp.sales.infrastructure.persistence.SalesCartItemJpaEntity;
import com.enterprise.erp.sales.infrastructure.persistence.SalesCartJpaEntity;
import com.enterprise.erp.sales.infrastructure.persistence.SalesCartJpaRepository;
import com.enterprise.erp.sales.infrastructure.persistence.SalesOrderJpaEntity;
import com.enterprise.erp.sales.infrastructure.persistence.SalesOrderJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckoutUseCaseTest {

    @Mock
    private SalesCartJpaRepository salesCartJpaRepository;

    @Mock
    private SalesOrderJpaRepository salesOrderJpaRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private ClientJpaRepository clientJpaRepository;

    @Mock
    private WarehouseJpaRepository warehouseJpaRepository;

    @Test
    void execute_shouldCreateOrderDiscountInventoryAndClearCart() {
        UserJpaEntity user = UserJpaEntity.builder().id(1L).username("seller").build();
        ClientJpaEntity client = ClientJpaEntity.builder().id(2L).type(ClientType.PERSON).name("Cliente").active(true).build();
        WarehouseJpaEntity warehouse = WarehouseJpaEntity.builder().id(5L).code("ALM-1").name("Principal").active(true).build();
        ProductJpaEntity product = ProductJpaEntity.builder().id(3L).sku("SKU-1").name("Producto").build();
        SalesCartJpaEntity cart = SalesCartJpaEntity.builder()
                .id(4L)
                .user(user)
                .items(new ArrayList<>())
                .build();
        cart.getItems().add(SalesCartItemJpaEntity.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(15))
                .build());

        SalesOrderJpaEntity savedOrder = SalesOrderJpaEntity.builder()
                .id(20L)
                .client(client)
                .user(user)
                .warehouse(warehouse)
                .status("COMPLETED")
                .total(BigDecimal.valueOf(30))
                .lines(new ArrayList<>())
                .build();
        when(userJpaRepository.findByUsername("seller")).thenReturn(Optional.of(user));
        when(salesCartJpaRepository.findLatestByUserIdWithItems(1L)).thenReturn(Optional.of(cart));
        when(clientJpaRepository.findById(2L)).thenReturn(Optional.of(client));
        when(warehouseJpaRepository.findById(5L)).thenReturn(Optional.of(warehouse));
        when(salesOrderJpaRepository.save(any(SalesOrderJpaEntity.class))).thenReturn(savedOrder);
        List<InventoryMovementRequest> movements = new ArrayList<>();
        ExitInventoryUseCase exitInventoryUseCase = new ExitInventoryUseCase(null, null) {
            @Override
            public InventoryMovementResponse execute(InventoryMovementRequest request) {
                movements.add(request);
                return null;
            }
        };
        CheckoutUseCase useCase = new CheckoutUseCase(
                salesCartJpaRepository,
                salesOrderJpaRepository,
                userJpaRepository,
                clientJpaRepository,
                warehouseJpaRepository,
                exitInventoryUseCase,
                new SalesMapper()
        );

        SalesOrderResponse result = useCase.execute("seller", new CheckoutRequest(2L, 5L));

        assertThat(movements).hasSize(1);
        assertThat(movements.getFirst().productId()).isEqualTo(3L);
        assertThat(movements.getFirst().warehouseId()).isEqualTo(5L);
        assertThat(movements.getFirst().quantity()).isEqualTo(2);
        assertThat(movements.getFirst().referenceType()).isEqualTo("SALES_ORDER");
        assertThat(cart.getItems()).isEmpty();
        verify(salesCartJpaRepository).save(cart);
        assertThat(result.id()).isEqualTo(20L);
        assertThat(result.clientId()).isEqualTo(2L);
        assertThat(result.total()).isEqualByComparingTo("30");
    }
}
