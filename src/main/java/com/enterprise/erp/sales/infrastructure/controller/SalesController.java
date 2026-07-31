package com.enterprise.erp.sales.infrastructure.controller;

import com.enterprise.erp.sales.application.dto.*;
import com.enterprise.erp.sales.application.usecase.*;
import com.enterprise.erp.shared.application.dto.PageResponse;
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
    private final UpdateCartItemUseCase updateCartItemUseCase;
    private final RemoveCartItemUseCase removeCartItemUseCase;
    private final ClearCartUseCase clearCartUseCase;
    private final GetSalesOrderUseCase getSalesOrderUseCase;
    private final CancelSalesOrderUseCase cancelSalesOrderUseCase;

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

    @GetMapping("/orders/{id}")
    @PreAuthorize("hasAuthority('SALE_READ')")
    @Operation(summary = "Obtener orden de venta")
    public ResponseEntity<SalesOrderResponse> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(getSalesOrderUseCase.execute(id));
    }

    @PatchMapping("/orders/{id}/cancel")
    @PreAuthorize("hasAuthority('SALE_WRITE')")
    @Operation(summary = "Cancelar orden de venta")
    public ResponseEntity<SalesOrderResponse> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(cancelSalesOrderUseCase.execute(id));
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

    @PutMapping("/cart/items/{itemId}")
    @PreAuthorize("hasAuthority('SALE_WRITE')")
    @Operation(summary = "Actualizar cantidad de un ítem del carrito")
    public ResponseEntity<CartResponse> updateItem(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(updateCartItemUseCase.execute(user.getUsername(), itemId, request));
    }

    @DeleteMapping("/cart/items/{itemId}")
    @PreAuthorize("hasAuthority('SALE_WRITE')")
    @Operation(summary = "Eliminar ítem del carrito")
    public ResponseEntity<CartResponse> removeItem(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(removeCartItemUseCase.execute(user.getUsername(), itemId));
    }

    @DeleteMapping("/cart")
    @PreAuthorize("hasAuthority('SALE_WRITE')")
    @Operation(summary = "Vaciar carrito")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails user) {
        clearCartUseCase.execute(user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAuthority('SALE_WRITE')")
    @Operation(summary = "Finalizar venta")
    public ResponseEntity<SalesOrderResponse> checkout(@AuthenticationPrincipal UserDetails user,
                                                       @Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(checkoutUseCase.execute(user.getUsername(), request));
    }
}
