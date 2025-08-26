package io.vitormmartins.chatforge.infrastructure.persistence.user.mapper;

import java.util.Optional;

import io.vitormmartins.chatforge.domain.user.model.*;
import io.vitormmartins.chatforge.infrastructure.persistence.user.entity.UserJpaEntity;

/**
 * Mapper between domain User entity and JPA UserJpaEntity.
 * Handles the conversion between domain and infrastructure models.
 */
public class UserMapper {

    private UserMapper() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Converts domain User to JPA entity.
     */
    public static UserJpaEntity toJpaEntity(User domainUser) {
        return new UserJpaEntity(
            domainUser.getUsername(),
            domainUser.getPassword(),
            domainUser.getEmail() != null ? domainUser.getEmail() : null,
            domainUser.getCreatedAt(),
            domainUser.getUpdatedAt()
        );
    }
    
    /**
     * Converts JPA entity to domain User.
     */
    public static User toDomainEntity(UserJpaEntity jpaEntity) {        
        return new User(
            Optional.of(jpaEntity.getId()),
            jpaEntity.getUsername(),
            jpaEntity.getEmail(),
            jpaEntity.getPassword(),
            jpaEntity.getCreatedAt());
    }
    
    /**
     * Updates JPA entity with data from domain User (for updates).
     */
    public static void updateJpaEntity(UserJpaEntity jpaEntity, User domainUser) {
        jpaEntity.setUsername(domainUser.getUsername());
        jpaEntity.setPassword(domainUser.getPassword());
        jpaEntity.setEmail(domainUser.getEmail());
        jpaEntity.setUpdatedAt(domainUser.getUpdatedAt());
    }
}
