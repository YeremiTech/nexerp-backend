package com.empresa.erp.roles.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionJpaRepository extends JpaRepository<PermissionJpaEntity, Long> {

    Optional<PermissionJpaEntity> findByCode(String code);

    List<PermissionJpaEntity> findAllByOrderByCodeAsc();
}
