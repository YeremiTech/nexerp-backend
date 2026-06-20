package com.empresa.erp.reportes.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface SalesDailyTotalProjection {

    LocalDate getSaleDate();

    long getOrders();

    BigDecimal getTotal();
}
