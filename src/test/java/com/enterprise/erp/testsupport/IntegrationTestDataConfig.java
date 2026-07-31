package com.enterprise.erp.testsupport;

import com.enterprise.erp.users.infrastructure.persistence.UserJpaEntity;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@TestConfiguration
public class IntegrationTestDataConfig {

    @Bean
    InitializingBean integrationTestAdminUser(UserJpaRepository userRepository,
                                              PasswordEncoder passwordEncoder) {
        return () -> {
            if (userRepository.existsByUsername("admin")) {
                return;
            }
            userRepository.save(UserJpaEntity.builder()
                    .username("admin")
                    .email("admin@integration.test")
                    .passwordHash(passwordEncoder.encode("admin!"))
                    .active(true)
                    .roles(new HashSet<>())
                    .build());
        };
    }
}
