package com.empresa.erp.dashboard.infrastructure.controller;

import com.empresa.erp.dashboard.application.dto.DashboardMetricsResponse;
import com.empresa.erp.dashboard.application.usecase.GetDashboardMetricsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
public class DashboardController {

    private final GetDashboardMetricsUseCase getDashboardMetricsUseCase;

    @GetMapping("/metrics")
    @PreAuthorize("hasAuthority('DASHBOARD_READ')")
    @Operation(summary = "Métricas del dashboard")
    public ResponseEntity<DashboardMetricsResponse> metrics() {
        return ResponseEntity.ok(getDashboardMetricsUseCase.execute());
    }
}
