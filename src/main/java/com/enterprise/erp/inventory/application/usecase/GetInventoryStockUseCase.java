package com.enterprise.erp.inventory.application.usecase;

import com.enterprise.erp.inventory.application.dto.InventoryStockResponse;
import com.enterprise.erp.inventory.infrastructure.persistence.InventoryItemJpaEntity;
import com.enterprise.erp.inventory.infrastructure.persistence.InventoryItemJpaRepository;
import com.enterprise.erp.inventory.infrastructure.persistence.WarehouseJpaRepository;
import com.enterprise.erp.products.infrastructure.persistence.ProductJpaRepository;
import com.enterprise.erp.shared.domain.exception.ResourceNotFoundException;
import com.enterprise.erp.shared.util.ListSearchSupport;
import com.enterprise.erp.shared.util.PageableSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetInventoryStockUseCase {

    private final InventoryItemJpaRepository inventoryItemJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final WarehouseJpaRepository warehouseJpaRepository;

    @Transactional(readOnly = true)
    public Page<InventoryStockResponse> list(String search, Long productId, Long warehouseId,
                                             Long categoryId, Boolean lowStock, Pageable pageable) {
        pageable = PageableSupport.newestFirst(pageable);
        return inventoryItemJpaRepository.search(
                        ListSearchSupport.toLikePattern(search),
                        productId,
                        warehouseId,
                        categoryId,
                        lowStock,
                        pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<InventoryStockResponse> byProduct(Long productId, Pageable pageable) {
        if (!productJpaRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Producto", productId);
        }
        pageable = PageableSupport.newestFirst(pageable);
        return inventoryItemJpaRepository.search(null, productId, null, null, null, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public InventoryStockResponse byProductAndWarehouse(Long productId, Long warehouseId) {
        if (!productJpaRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Producto", productId);
        }
        if (!warehouseJpaRepository.existsById(warehouseId)) {
            throw new ResourceNotFoundException("Almacén", warehouseId);
        }
        return inventoryItemJpaRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Existencia de producto " + productId + " en almacén", warehouseId));
    }

    private InventoryStockResponse toResponse(InventoryItemJpaEntity item) {
        var product = item.getProduct();
        var warehouse = item.getWarehouse();
        return new InventoryStockResponse(
                item.getId(),
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getCategory() == null ? null : product.getCategory().getId(),
                warehouse.getId(),
                warehouse.getCode(),
                warehouse.getName(),
                item.getQuantity(),
                product.getMinStock(),
                item.getQuantity() < product.getMinStock());
    }
}
