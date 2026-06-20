package com.empresa.erp.dashboard.application.usecase;

import com.empresa.erp.clientes.infrastructure.persistence.ClientJpaRepository;
import com.empresa.erp.dashboard.application.dto.DashboardMetricsResponse;
import com.empresa.erp.infrastructure.config.RedisConfig;
import com.empresa.erp.inventario.infrastructure.persistence.InventoryItemJpaRepository;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaRepository;
import com.empresa.erp.proveedores.infrastructure.persistence.SupplierJpaRepository;
import com.empresa.erp.ventas.infrastructure.persistence.SalesOrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class GetDashboardMetricsUseCase {

    private final ProductJpaRepository productJpaRepository;
    private final ClientJpaRepository clientJpaRepository;
    private final SupplierJpaRepository supplierJpaRepository;
    private final SalesOrderJpaRepository salesOrderJpaRepository;
    private final InventoryItemJpaRepository inventoryItemJpaRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = RedisConfig.CACHE_DASHBOARD, key = "'metrics'")
    public DashboardMetricsResponse execute() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        long salesToday = salesOrderJpaRepository.countByCreatedAtBetweenAndStatus(startOfDay, endOfDay, "COMPLETED");
        BigDecimal salesTotal = salesOrderJpaRepository.sumTotalByCreatedAtBetweenAndStatus(startOfDay, endOfDay, "COMPLETED");

        return new DashboardMetricsResponse(
                productJpaRepository.countByActiveTrue(),
                clientJpaRepository.countByActiveTrue(),
                supplierJpaRepository.countByActiveTrue(),
                salesToday,
                salesTotal != null ? salesTotal : BigDecimal.ZERO,
                inventoryItemJpaRepository.countLowStockItems());
    }
}
