package com.enterprise.erp.infrastructure.config;

import com.enterprise.erp.roles.infrastructure.persistence.RoleJpaEntity;
import com.enterprise.erp.roles.infrastructure.persistence.RoleJpaRepository;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaEntity;
import com.enterprise.erp.users.infrastructure.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Component
@ConditionalOnProperty(name = "app.local.bootstrap-enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class LocalDataInitializer implements ApplicationRunner {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.local.admin.username}")
    private String adminUsername;

    @Value("${app.local.admin.email}")
    private String adminEmail;

    @Value("${app.local.admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userJpaRepository.findByUsername(adminUsername).isPresent()) {
            return;
        }

        RoleJpaEntity adminRole = roleJpaRepository.findByName("ADMIN")
                .orElseGet(() -> roleJpaRepository.save(RoleJpaEntity.builder()
                        .name("ADMIN")
                        .description("Administrador con acceso completo")
                        .active(true)
                        .build()));

        UserJpaEntity admin = UserJpaEntity.builder()
                .username(adminUsername)
                .email(adminEmail)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .active(true)
                .roles(new HashSet<>())
                .build();
        admin.getRoles().add(adminRole);

        userJpaRepository.save(admin);
    }
}
