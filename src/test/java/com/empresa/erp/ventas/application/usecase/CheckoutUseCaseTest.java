package com.empresa.erp.ventas.application.usecase;

import com.empresa.erp.clientes.domain.ClientType;
import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaEntity;
import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaRepository;
import com.empresa.erp.inventario.application.dto.InventoryMovementRequest;
import com.empresa.erp.inventario.application.dto.InventoryMovementResponse;
import com.empresa.erp.inventario.application.usecase.ExitInventoryUseCase;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaEntity;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaEntity;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaRepository;
import com.empresa.erp.ventas.application.dto.CheckoutRequest;
import com.empresa.erp.ventas.application.dto.SalesOrderResponse;
import com.empresa.erp.ventas.application.mapper.SalesMapper;
import com.empresa.erp.ventas.infrastructure.persistence.SalesCartItemJpaEntity;
import com.empresa.erp.ventas.infrastructure.persistence.SalesCartJpaEntity;
import com.empresa.erp.ventas.infrastructure.persistence.SalesCartJpaRepository;
import com.empresa.erp.ventas.infrastructure.persistence.SalesOrderJpaEntity;
import com.empresa.erp.ventas.infrastructure.persistence.SalesOrderJpaRepository;
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

    @Test
    void execute_shouldCreateOrderDiscountInventoryAndClearCart() {
        UserJpaEntity user = UserJpaEntity.builder().id(1L).username("seller").build();
        ClientJpaEntity client = ClientJpaEntity.builder().id(2L).type(ClientType.PERSON).name("Cliente").active(true).build();
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
                .status("COMPLETED")
                .total(BigDecimal.valueOf(30))
                .lines(new ArrayList<>())
                .build();
        when(userJpaRepository.findByUsername("seller")).thenReturn(Optional.of(user));
        when(salesCartJpaRepository.findLatestByUserIdWithItems(1L)).thenReturn(Optional.of(cart));
        when(clientJpaRepository.findById(2L)).thenReturn(Optional.of(client));
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
