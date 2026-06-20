package com.empresa.erp.clientes.infrastructure.controller;

import com.empresa.erp.clientes.application.dto.*;
import com.empresa.erp.clientes.application.usecase.*;
import com.empresa.erp.shared.application.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Clientes")
public class ClientController {

    private final CreateClientUseCase createClientUseCase;
    private final UpdateClientUseCase updateClientUseCase;
    private final GetClientUseCase getClientUseCase;
    private final ListClientsUseCase listClientsUseCase;
    private final GetClientPurchaseHistoryUseCase getClientPurchaseHistoryUseCase;

    @GetMapping
    @PreAuthorize("hasAuthority('CLIENT_READ')")
    @Operation(summary = "Listar clientes")
    public ResponseEntity<PageResponse<ClientResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(listClientsUseCase.execute(search, type, active, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT_READ')")
    @Operation(summary = "Obtener cliente")
    public ResponseEntity<ClientResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(getClientUseCase.execute(id));
    }

    @GetMapping("/{id}/purchases")
    @PreAuthorize("hasAuthority('CLIENT_READ')")
    @Operation(summary = "Historial de compras del cliente")
    public ResponseEntity<PageResponse<ClientPurchaseHistoryItem>> purchaseHistory(@PathVariable Long id,
            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(getClientPurchaseHistoryUseCase.execute(id, pageable)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT_WRITE')")
    @Operation(summary = "Crear cliente")
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody CreateClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createClientUseCase.execute(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT_WRITE')")
    @Operation(summary = "Actualizar cliente")
    public ResponseEntity<ClientResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody UpdateClientRequest request) {
        return ResponseEntity.ok(updateClientUseCase.execute(id, request));
    }
}
