package io.vitormmartins.chatforge.infrastructure.persistence.user.repository;

import io.vitormmartins.chatforge.infrastructure.persistence.user.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for UserJpaEntity.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    
    Optional<UserJpaEntity> findByUsername(String username);
    
    boolean existsByUsername(String username);
}
