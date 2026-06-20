package com.empresa.erp.productos.application.mapper;

import com.empresa.erp.productos.application.dto.ProductResponse;
import com.empresa.erp.productos.infrastructure.persistence.ProductJpaEntity;
import com.empresa.erp.productos.infrastructure.persistence.ProductPriceJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "currentPrice", expression = "java(currentPrice(entity))")
    @Mapping(target = "currency", expression = "java(currentCurrency(entity))")
    @Mapping(target = "imageUrls", expression = "java(mapImages(entity))")
    ProductResponse toResponse(ProductJpaEntity entity);

    default BigDecimal currentPrice(ProductJpaEntity entity) {
        if (entity.getPrices() == null || entity.getPrices().isEmpty()) {
            return null;
        }
        return entity.getPrices().stream()
                .filter(p -> p.getValidTo() == null || p.getValidTo().isAfter(LocalDateTime.now()))
                .max(Comparator.comparing(ProductPriceJpaEntity::getValidFrom))
                .map(ProductPriceJpaEntity::getPrice)
                .orElse(null);
    }

    default String currentCurrency(ProductJpaEntity entity) {
        if (entity.getPrices() == null || entity.getPrices().isEmpty()) {
            return "PEN";
        }
        return entity.getPrices().stream()
                .filter(p -> p.getValidTo() == null || p.getValidTo().isAfter(LocalDateTime.now()))
                .max(Comparator.comparing(ProductPriceJpaEntity::getValidFrom))
                .map(ProductPriceJpaEntity::getCurrency)
                .orElse("PEN");
    }

    default List<String> mapImages(ProductJpaEntity entity) {
        return entity.getImages().stream()
                .sorted(Comparator.comparingInt(i -> i.getSortOrder()))
                .map(i -> i.getUrl())
                .toList();
    }
}
