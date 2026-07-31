package com.enterprise.erp.infrastructure.config;

import com.enterprise.erp.shared.util.PageableSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class PaginationConfig {

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer() {
        return resolver -> resolver.setFallbackPageable(
                PageRequest.of(0, PageableSupport.DEFAULT_PAGE_SIZE, PageableSupport.NEWEST_FIRST)
        );
    }
}
