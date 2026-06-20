package com.empresa.erp.productos.application.usecase;

import com.empresa.erp.categorias.infrastructure.persistence.CategoryJpaRepository;
import com.empresa.erp.productos.application.dto.ProductResponse;
import com.empresa.erp.productos.application.dto.UpdateProductRequest;
import com.empresa.erp.productos.application.mapper.ProductMapper;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaEntity;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaRepository;
import com.empresa.erp.productos.infrastructure.persistence.ProductPriceJpaEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProductUseCaseTest {

    @Mock
    private ProductJpaRepository productJpaRepository;

    @Mock
    private CategoryJpaRepository categoryJpaRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private UpdateProductUseCase useCase;

    @Test
    void execute_shouldCloseCurrentPriceAndCreateNewPrice() {
        ProductJpaEntity product = ProductJpaEntity.builder()
                .id(1L)
                .sku("SKU-001")
                .name("Producto")
                .minStock(5)
                .active(true)
                .prices(new ArrayList<>())
                .images(new ArrayList<>())
                .build();
        product.getPrices().add(ProductPriceJpaEntity.builder()
                .product(product)
                .price(BigDecimal.TEN)
                .currency("PEN")
                .validTo(null)
                .build());

        UpdateProductRequest request = new UpdateProductRequest(
                null,
                null,
                null,
                null,
                null,
                BigDecimal.valueOf(20),
                "USD",
                List.of("https://example.com/image.png")
        );
        ProductResponse response = new ProductResponse(1L, "SKU-001", "Producto", null, null, null, 5, true,
                BigDecimal.valueOf(20), "USD", List.of("https://example.com/image.png"));

        when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productJpaRepository.save(any(ProductJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productMapper.toResponse(any(ProductJpaEntity.class))).thenReturn(response);

        ProductResponse result = useCase.execute(1L, request);

        assertThat(product.getPrices()).hasSize(2);
        assertThat(product.getPrices().get(0).getValidTo()).isNotNull();
        assertThat(product.getPrices().get(1).getPrice()).isEqualByComparingTo("20");
        assertThat(product.getPrices().get(1).getCurrency()).isEqualTo("USD");
        assertThat(product.getImages()).hasSize(1);
        assertThat(result).isEqualTo(response);
    }
}
