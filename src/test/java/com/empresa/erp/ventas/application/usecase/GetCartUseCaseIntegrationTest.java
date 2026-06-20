package com.empresa.erp.ventas.application.usecase;

import com.empresa.erp.categorias.infrastructure.persistence.CategoryJpaEntity;
import com.empresa.erp.categorias.infrastructure.persistence.CategoryJpaRepository;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaEntity;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaRepository;
import com.empresa.erp.productos.infrastructure.persistence.ProductPriceJpaEntity;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaEntity;
import com.empresa.erp.usuarios.infrastructure.persistence.UserJpaRepository;
import com.empresa.erp.ventas.application.dto.AddCartItemRequest;
import com.empresa.erp.ventas.application.dto.CartResponse;
import com.empresa.erp.ventas.infrastructure.persistence.SalesCartJpaEntity;
import com.empresa.erp.ventas.infrastructure.persistence.SalesCartJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ActiveProfiles("test")
class GetCartUseCaseIntegrationTest {

    @Autowired
    private GetCartUseCase getCartUseCase;

    @Autowired
    private AddCartItemUseCase addCartItemUseCase;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private CategoryJpaRepository categoryJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SalesCartJpaRepository salesCartJpaRepository;

    private String username;
    private Long productId;

    @BeforeEach
    void seedData() {
        username = userJpaRepository.findAll().stream()
                .map(UserJpaEntity::getUsername)
                .findFirst()
                .orElseGet(() -> userJpaRepository.save(UserJpaEntity.builder()
                        .username("seller-cart-get")
                        .email("seller-cart-get@test.local")
                        .passwordHash(passwordEncoder.encode("seller!"))
                        .active(true)
                        .roles(new HashSet<>())
                        .build()).getUsername());

        if (productJpaRepository.count() == 0) {
            CategoryJpaEntity category = categoryJpaRepository.save(CategoryJpaEntity.builder()
                    .name("Test")
                    .active(true)
                    .build());
            ProductJpaEntity product = productJpaRepository.save(ProductJpaEntity.builder()
                    .sku("SKU-GET-CART")
                    .name("Producto Get Cart")
                    .minStock(1)
                    .active(true)
                    .category(category)
                    .build());
            product.getPrices().add(ProductPriceJpaEntity.builder()
                    .product(product)
                    .price(new BigDecimal("15.00"))
                    .currency("PEN")
                    .validFrom(LocalDateTime.now().minusDays(1))
                    .build());
            productId = productJpaRepository.save(product).getId();
        } else {
            productId = productJpaRepository.findAll().getFirst().getId();
        }
    }

    @Test
    @Transactional
    void execute_shouldReturnEmptyCartWhenUserHasNoCart() {
        assertThatCode(() -> {
            CartResponse response = getCartUseCase.execute(username);
            assertThat(response.items()).isEmpty();
            assertThat(response.total()).isEqualByComparingTo(BigDecimal.ZERO);
        }).doesNotThrowAnyException();
    }

    @Test
    @Transactional
    void execute_shouldReturnCartWithItemsAfterAdd() {
        addCartItemUseCase.execute(username, new AddCartItemRequest(productId, 2));

        assertThatCode(() -> {
            CartResponse response = getCartUseCase.execute(username);
            assertThat(response.items()).hasSize(1);
            assertThat(response.items().getFirst().quantity()).isEqualTo(2);
        }).doesNotThrowAnyException();
    }

    @Test
    @Transactional
    void execute_shouldReturnLatestCartWhenUserHasMultipleCarts() {
        var user = userJpaRepository.findByUsername(username).orElseThrow();
        salesCartJpaRepository.save(SalesCartJpaEntity.builder().user(user).build());
        addCartItemUseCase.execute(username, new AddCartItemRequest(productId, 1));

        assertThatCode(() -> {
            CartResponse response = getCartUseCase.execute(username);
            assertThat(response.items()).hasSize(1);
        }).doesNotThrowAnyException();
    }
}
