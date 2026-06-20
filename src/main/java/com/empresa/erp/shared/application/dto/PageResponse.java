package com.empresa.erp.shared.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Contrato unificado de paginación para la API ERP.
 * <p>
 * {@code number} es el índice de página basado en cero (Spring Data).
 * Query params: {@code page}, {@code size}, {@code sort=campo,asc|desc}.
 */
@Schema(description = "Respuesta paginada")
public record PageResponse<T>(
        @Schema(description = "Elementos de la página actual") List<T> content,
        @Schema(description = "Índice de página (0-based)", example = "0") int number,
        @Schema(description = "Tamaño de página", example = "10") int size,
        @Schema(description = "Total de elementos en todas las páginas") long totalElements,
        @Schema(description = "Total de páginas") int totalPages,
        @Schema(description = "Es la primera página") boolean first,
        @Schema(description = "Es la última página") boolean last,
        @Schema(description = "La página no tiene elementos") boolean empty
) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty()
        );
    }
}
