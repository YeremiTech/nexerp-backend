package com.empresa.erp.ventas.infrastructure.controller;

import com.empresa.erp.categorias.infrastructure.persistence.CategoryJpaEntity;
import com.empresa.erp.categorias.infrastructure.persistence.CategoryJpaRepository;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaEntity;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaRepository;
import com.empresa.erp.productos.infrastructure.persistence.ProductPriceJpaEntity;
import com.empresa.erp.roles.infrastructure.persistence.PermissionJpaEntity;
import com.empresa.erp.roles.infrastructure.persistence.PermissionJpaRepository;
import com.empresa.erp.roles.infrastructure.persistence.RoleJpaEntity;
import com.empresa.erp.roles.infrastructure.persistence.RoleJpaRepository;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaEntity;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SalesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private RoleJpaRepository roleJpaRepository;

    @Autowired
    private PermissionJpaRepository permissionJpaRepository;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private CategoryJpaRepository categoryJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long productId;

    @BeforeEach
    void seedSeller() {
        if (productJpaRepository.count() == 0) {
            CategoryJpaEntity category = categoryJpaRepository.save(CategoryJpaEntity.builder()
                    .name("Ventas Test")
                    .active(true)
                    .build());
            ProductJpaEntity product = productJpaRepository.save(ProductJpaEntity.builder()
                    .sku("SKU-SALES-CTRL")
                    .name("Producto Sales Controller")
                    .minStock(1)
                    .active(true)
                    .category(category)
                    .build());
            product.getPrices().add(ProductPriceJpaEntity.builder()
                    .product(product)
                    .price(new BigDecimal("99.90"))
                    .currency("PEN")
                    .validFrom(LocalDateTime.now().minusDays(1))
                    .build());
            productId = productJpaRepository.save(product).getId();
        } else {
            productId = productJpaRepository.findAll().getFirst().getId();
        }

        if (!userJpaRepository.existsByUsername("seller-sales")) {
            PermissionJpaEntity saleRead = permissionJpaRepository.findByCode("SALE_READ")
                    .orElseGet(() -> permissionJpaRepository.save(PermissionJpaEntity.builder()
                            .code("SALE_READ")
                            .description("Leer ventas")
                            .build()));
            PermissionJpaEntity saleWrite = permissionJpaRepository.findByCode("SALE_WRITE")
                    .orElseGet(() -> permissionJpaRepository.save(PermissionJpaEntity.builder()
                            .code("SALE_WRITE")
                            .description("Escribir ventas")
                            .build()));
            RoleJpaEntity sellerRole = roleJpaRepository.save(RoleJpaEntity.builder()
                    .name("SELLER_TEST")
                    .active(true)
                    .permissions(Set.of(saleRead, saleWrite))
                    .build());
            userJpaRepository.save(UserJpaEntity.builder()
                    .username("seller-sales")
                    .email("seller-sales@test.local")
                    .passwordHash(passwordEncoder.encode("seller!"))
                    .active(true)
                    .roles(Set.of(sellerRole))
                    .build());
        }
    }

    @Test
    void cartEndpoints_shouldWorkForAuthenticatedSeller() throws Exception {
        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"seller-sales","password":"seller!"}
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = loginResponse.replaceAll(".*\"accessToken\"\\s*:\\s*\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/api/v1/sales/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());

        mockMvc.perform(post("/api/v1/sales/cart/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productId":%d,"quantity":2}
                                """.formatted(productId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }
}
