package com.enterprise.erp.reports.application.usecase;

import com.enterprise.erp.inventory.infrastructure.persistence.InventoryItemJpaRepository;
import com.enterprise.erp.reports.application.dto.InventoryReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetInventoryReportUseCase {

    private final InventoryItemJpaRepository inventoryItemJpaRepository;

    @Transactional(readOnly = true)
    public InventoryReportResponse execute() {
        var items = inventoryItemJpaRepository.findStockReport().stream()
                .map(item -> new InventoryReportResponse.StockItem(
                        item.getProductId(),
                        item.getProductSku(),
                        item.getProductName(),
                        item.getWarehouseId(),
                        item.getWarehouseCode(),
                        item.getQuantity(),
                        item.getMinStock(),
                        item.getQuantity() < item.getMinStock()))
                .toList();

        long totalUnits = items.stream().mapToLong(InventoryReportResponse.StockItem::quantity).sum();
        return new InventoryReportResponse(items.size(), totalUnits, items);
    }
}
