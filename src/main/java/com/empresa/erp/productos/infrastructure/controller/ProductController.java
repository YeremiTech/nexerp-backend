package com.empresa.erp.productos.infrastructure.controller;

import com.empresa.erp.productos.application.dto.CreateProductRequest;
import com.empresa.erp.productos.application.dto.ProductResponse;
import com.empresa.erp.productos.application.dto.ProductSkuPreviewResponse;
import com.empresa.erp.productos.application.dto.UpdateProductRequest;
import com.empresa.erp.productos.application.usecase.*;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Productos")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final PreviewProductSkuUseCase previewProductSkuUseCase;

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Listar productos")
    public ResponseEntity<PageResponse<ProductResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(listProductsUseCase.execute(search, categoryId, active, pageable)));
    }

    @GetMapping("/sku-preview")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Previsualizar SKU segun nombre del producto")
    public ResponseEntity<ProductSkuPreviewResponse> previewSku(@RequestParam String name) {
        return ResponseEntity.ok(previewProductSkuUseCase.execute(name));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Obtener producto")
    public ResponseEntity<ProductResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(getProductUseCase.execute(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    @Operation(summary = "Crear producto")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createProductUseCase.execute(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    @Operation(summary = "Actualizar producto")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(updateProductUseCase.execute(id, request));
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    @Operation(summary = "Actualizar estado del producto")
    public ResponseEntity<ProductResponse> setActive(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(updateProductUseCase.execute(id, new UpdateProductRequest(null, null, null, null, active, null, null, null)));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    @Operation(summary = "Activar producto")
    public ResponseEntity<ProductResponse> activate(@PathVariable Long id) {
        return ResponseEntity.ok(updateProductUseCase.execute(id, new UpdateProductRequest(null, null, null, null, true, null, null, null)));
    }

    @PatchMapping("/{id}/inactivar")
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    @Operation(summary = "Inactivar producto")
    public ResponseEntity<ProductResponse> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(updateProductUseCase.execute(id, new UpdateProductRequest(null, null, null, null, false, null, null, null)));
    }
}
