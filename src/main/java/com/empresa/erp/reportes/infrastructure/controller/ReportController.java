package com.empresa.erp.reportes.infrastructure.controller;

import com.empresa.erp.reportes.application.dto.InventoryReportResponse;
import com.empresa.erp.reportes.application.dto.SalesReportResponse;
import com.empresa.erp.reportes.application.usecase.GetInventoryReportUseCase;
import com.empresa.erp.reportes.application.usecase.GetSalesReportUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reportes")
public class ReportController {

    private final GetSalesReportUseCase getSalesReportUseCase;
    private final GetInventoryReportUseCase getInventoryReportUseCase;

    @GetMapping("/sales")
    @PreAuthorize("hasAuthority('REPORT_READ')")
    @Operation(summary = "Reporte de ventas")
    public ResponseEntity<SalesReportResponse> sales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(getSalesReportUseCase.execute(from, to));
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasAuthority('REPORT_READ')")
    @Operation(summary = "Reporte de inventario")
    public ResponseEntity<InventoryReportResponse> inventory() {
        return ResponseEntity.ok(getInventoryReportUseCase.execute());
    }
}
