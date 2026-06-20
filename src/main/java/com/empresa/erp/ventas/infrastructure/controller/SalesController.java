package com.empresa.erp.ventas.infrastructure.controller;

import com.empresa.erp.ventas.application.dto.*;
import com.empresa.erp.ventas.application.usecase.AddCartItemUseCase;
import com.empresa.erp.ventas.application.usecase.CheckoutUseCase;
import com.empresa.erp.ventas.application.usecase.GetCartUseCase;
import com.empresa.erp.ventas.application.usecase.ListSalesOrdersUseCase;
import com.empresa.erp.shared.application.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@Tag(name = "Ventas")
public class SalesController {

    private final AddCartItemUseCase addCartItemUseCase;
    private final GetCartUseCase getCartUseCase;
    private final CheckoutUseCase checkoutUseCase;
    private final ListSalesOrdersUseCase listSalesOrdersUseCase;

    @GetMapping("/orders")
    @PreAuthorize("hasAuthority('SALE_READ')")
    @Operation(summary = "Listar ordenes de venta")
    public ResponseEntity<PageResponse<SalesOrderListItem>> listOrders(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(listSalesOrdersUseCase.execute(search, status, from, to, pageable)));
    }

    @GetMapping("/cart")
    @PreAuthorize("hasAuthority('SALE_READ')")
    @Operation(summary = "Obtener carrito del usuario")
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(getCartUseCase.execute(user.getUsername()));
    }

    @PostMapping("/cart/items")
    @PreAuthorize("hasAuthority('SALE_WRITE')")
    @Operation(summary = "Agregar item al carrito")
    public ResponseEntity<CartResponse> addItem(@AuthenticationPrincipal UserDetails user,
                                                  @Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(addCartItemUseCase.execute(user.getUsername(), request));
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAuthority('SALE_WRITE')")
    @Operation(summary = "Finalizar venta")
    public ResponseEntity<SalesOrderResponse> checkout(@AuthenticationPrincipal UserDetails user,
                                                       @Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(checkoutUseCase.execute(user.getUsername(), request));
    }
}
