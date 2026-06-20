package com.empresa.erp.usuarios.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByUsername(String username);

    Optional<UserJpaEntity> findByEmail(String email);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<UserJpaEntity> findByUsernameOrEmail(@Param("identifier") String identifier);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Page<UserJpaEntity> findByActive(boolean active, Pageable pageable);

    @Query("""
            SELECT u FROM UserJpaEntity u
            WHERE (:active IS NULL OR u.active = :active)
              AND (
                    :searchPattern IS NULL OR
                    LOWER(u.username) LIKE :searchPattern OR
                    LOWER(u.email) LIKE :searchPattern
                  )
            """)
    Page<UserJpaEntity> search(
            @Param("searchPattern") String searchPattern,
            @Param("active") Boolean active,
            Pageable pageable);
}
