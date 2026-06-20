package com.empresa.erp.inventario.application.service;

import com.empresa.erp.inventario.domain.InventoryMovementType;
import com.empresa.erp.inventario.infrastructure.persistence.InventoryItemJpaEntity;
import com.empresa.erp.inventario.infrastructure.persistence.InventoryItemJpaRepository;
import com.empresa.erp.inventario.infrastructure.persistence.InventoryMovementJpaEntity;
import com.empresa.erp.inventario.infrastructure.persistence.InventoryMovementJpaRepository;
import com.empresa.erp.inventario.infrastructure.persistence.WarehouseJpaEntity;
import com.empresa.erp.inventario.infrastructure.persistence.WarehouseJpaRepository;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaEntity;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaRepository;
import com.empresa.erp.shared.application.constants.ApiErrorCode;
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryStockServiceTest {

    @Mock
    private InventoryItemJpaRepository inventoryItemJpaRepository;

    @Mock
    private InventoryMovementJpaRepository inventoryMovementJpaRepository;

    @Mock
    private ProductJpaRepository productJpaRepository;

    @Mock
    private WarehouseJpaRepository warehouseJpaRepository;

    @InjectMocks
    private InventoryStockService service;

    @Test
    void applyMovement_shouldIncreaseStockAndRecordMovement() {
        ProductJpaEntity product = ProductJpaEntity.builder().id(1L).sku("SKU-1").name("Producto").build();
        WarehouseJpaEntity warehouse = WarehouseJpaEntity.builder().id(2L).code("ALM-1").name("Central").build();
        InventoryItemJpaEntity item = InventoryItemJpaEntity.builder()
                .product(product)
                .warehouse(warehouse)
                .quantity(5)
                .build();

        when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseJpaRepository.findById(2L)).thenReturn(Optional.of(warehouse));
        when(inventoryItemJpaRepository.findByProductIdAndWarehouseIdForUpdate(1L, 2L)).thenReturn(Optional.of(item));
        when(inventoryMovementJpaRepository.save(any(InventoryMovementJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.applyMovement(1L, 2L, 3, InventoryMovementType.ENTRY, "TEST", 99L);

        assertThat(item.getQuantity()).isEqualTo(8);
        ArgumentCaptor<InventoryMovementJpaEntity> captor = ArgumentCaptor.forClass(InventoryMovementJpaEntity.class);
        verify(inventoryMovementJpaRepository).save(captor.capture());
        assertThat(captor.getValue().getType()).isEqualTo(InventoryMovementType.ENTRY);
        assertThat(captor.getValue().getQuantity()).isEqualTo(3);
    }

    @Test
    void applyMovement_shouldRejectInsufficientStock() {
        ProductJpaEntity product = ProductJpaEntity.builder().id(1L).sku("SKU-1").name("Producto").build();
        WarehouseJpaEntity warehouse = WarehouseJpaEntity.builder().id(2L).code("ALM-1").name("Central").build();
        InventoryItemJpaEntity item = InventoryItemJpaEntity.builder()
                .product(product)
                .warehouse(warehouse)
                .quantity(2)
                .build();

        when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseJpaRepository.findById(2L)).thenReturn(Optional.of(warehouse));
        when(inventoryItemJpaRepository.findByProductIdAndWarehouseIdForUpdate(1L, 2L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> service.applyMovement(1L, 2L, -3, InventoryMovementType.EXIT, "TEST", 99L))
                .isInstanceOf(BusinessRuleException.class)
                .extracting(ex -> ((BusinessRuleException) ex).getErrorCode())
                .isEqualTo(ApiErrorCode.INSUFFICIENT_STOCK.getCode());
        verify(inventoryMovementJpaRepository, never()).save(any());
    }
}
