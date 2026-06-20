package com.empresa.erp.categorias.infrastructure.controller;

import com.empresa.erp.categorias.application.dto.CategoryResponse;
import com.empresa.erp.categorias.application.dto.CreateCategoryRequest;
import com.empresa.erp.categorias.application.dto.UpdateCategoryRequest;
import com.empresa.erp.categorias.application.usecase.*;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categorías")
public class CategoryController {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final GetCategoryUseCase getCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;

    @GetMapping
    @PreAuthorize("hasAuthority('CATEGORY_READ')")
    @Operation(summary = "Listar categorías")
    public ResponseEntity<PageResponse<CategoryResponse>> list(
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(listCategoriesUseCase.execute(active, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_READ')")
    @Operation(summary = "Obtener categoría")
    public ResponseEntity<CategoryResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(getCategoryUseCase.execute(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CATEGORY_WRITE')")
    @Operation(summary = "Crear categoría")
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createCategoryUseCase.execute(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_WRITE')")
    @Operation(summary = "Actualizar categoría")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(updateCategoryUseCase.execute(id, request));
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasAuthority('CATEGORY_WRITE')")
    @Operation(summary = "Actualizar estado de la categoría")
    public ResponseEntity<CategoryResponse> setActive(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(updateCategoryUseCase.execute(id, new UpdateCategoryRequest(null, null, active)));
    }
}
