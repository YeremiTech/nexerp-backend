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
import com.empresa.erp.shared.domain.exception.BusinessRuleException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class AddCartItemUseCaseIntegrationTest {

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

    private Long productId;

    @BeforeEach
    void seedProduct() {
        if (productJpaRepository.count() > 0) {
            productId = productJpaRepository.findAll().getFirst().getId();
            return;
        }
        UserJpaEntity seller = userJpaRepository.save(UserJpaEntity.builder()
                .username("seller-cart")
                .email("seller-cart@test.local")
                .passwordHash(passwordEncoder.encode("seller!"))
                .active(true)
                .roles(new HashSet<>())
                .build());
        CategoryJpaEntity category = categoryJpaRepository.save(CategoryJpaEntity.builder()
                .name("Test")
                .active(true)
                .build());
        ProductJpaEntity product = productJpaRepository.save(ProductJpaEntity.builder()
                .sku("SKU-TEST-001")
                .name("Producto Test")
                .minStock(1)
                .active(true)
                .category(category)
                .build());
        product.getPrices().add(ProductPriceJpaEntity.builder()
                .product(product)
                .price(new BigDecimal("25.50"))
                .currency("PEN")
                .validFrom(LocalDateTime.now().minusDays(1))
                .build());
        productId = productJpaRepository.save(product).getId();
        userJpaRepository.save(seller);
    }

    @Test
    @Transactional
    void execute_shouldAddItemToCart() {
        String username = userJpaRepository.findAll().stream()
                .map(UserJpaEntity::getUsername)
                .findFirst()
                .orElse("seller-cart");

        assertThatCode(() -> {
            CartResponse response = addCartItemUseCase.execute(username, new AddCartItemRequest(productId, 2));
            assertThat(response.items()).hasSize(1);
            assertThat(response.items().getFirst().quantity()).isEqualTo(2);
        }).doesNotThrowAnyException();
    }

    @Test
    @Transactional
    void execute_shouldRejectProductWithoutActivePrice() {
        ProductJpaEntity productWithoutPrice = productJpaRepository.save(ProductJpaEntity.builder()
                .sku("SKU-NO-PRICE")
                .name("Sin precio")
                .minStock(1)
                .active(true)
                .category(categoryJpaRepository.findAll().getFirst())
                .build());

        String username = userJpaRepository.findAll().stream()
                .map(UserJpaEntity::getUsername)
                .findFirst()
                .orElse("seller-cart");

        assertThatThrownBy(() -> addCartItemUseCase.execute(
                username,
                new AddCartItemRequest(productWithoutPrice.getId(), 1)))
                .isInstanceOf(BusinessRuleException.class);
    }
}
