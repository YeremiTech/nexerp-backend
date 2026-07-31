package com.enterprise.erp.reports.application.dto;

public interface InventoryStockProjection {

    Long getProductId();

    String getProductSku();

    String getProductName();

    Long getWarehouseId();

    String getWarehouseCode();

    int getQuantity();

    int getMinStock();
}
