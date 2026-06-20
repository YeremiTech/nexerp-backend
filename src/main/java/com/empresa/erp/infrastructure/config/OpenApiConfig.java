package com.empresa.erp.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String PAGINATION_DOC = """

            ## Paginación
            Los listados paginados aceptan query params estándar de Spring Data:
            - `page`: índice de página (**0-based**, primera página = 0)
            - `size`: elementos por página (máximo 100)
            - `sort`: campo y dirección, ej. `sort=name,asc` o `sort=createdAt,desc`

            La respuesta usa `PageResponse` con campos: `content`, `number`, `size`,
            `totalElements`, `totalPages`, `first`, `last`, `empty`.

            ### Filtros opcionales en listados
            - `search`: texto libre (nombre, código, email, etc.)
            - `active`: true | false (clientes, proveedores, productos, usuarios)
            - `type`: PERSON | COMPANY (clientes)
            - `hasTaxId`: true | false (proveedores)
            - `categoryId`: ID numérico (productos)
            - `status`: estado de orden (ventas, compras)
            - `from` / `to`: rango de fechas ISO yyyy-MM-dd (ventas)
            """;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ERP PYMES API")
                        .version("1.0.0")
                        .description(PAGINATION_DOC))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"))
                .components(new Components().addSecuritySchemes("Bearer",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
