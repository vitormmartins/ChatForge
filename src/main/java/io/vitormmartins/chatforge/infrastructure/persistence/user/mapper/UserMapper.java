package io.vitormmartins.chatforge.infrastructure.persistence.user.mapper;

import io.vitormmartins.chatforge.domain.user.model.*;
import io.vitormmartins.chatforge.infrastructure.persistence.user.entity.UserJpaEntity;

/**
 * Mapper between domain User entity and JPA UserJpaEntity.
 * Handles the conversion between domain and infrastructure models.
 */
public class UserMapper {
    
    /**
     * Converts domain User to JPA entity.
     */
    public static UserJpaEntity toJpaEntity(User domainUser) {
        return new UserJpaEntity(
            domainUser.getUsername().getValue(),
            domainUser.getPassword().getEncodedValue(),
            domainUser.getEmail() != null ? domainUser.getEmail().getValue() : null,
            domainUser.getCreatedAt(),
            domainUser.getUpdatedAt()
        );
    }
    
    /**
     * Converts JPA entity to domain User.
     */
    public static User toDomainEntity(UserJpaEntity jpaEntity) {
        UserId id = jpaEntity.getId() != null ? new UserId(jpaEntity.getId()) : null;
        Username username = new Username(jpaEntity.getUsername());
        Email email = jpaEntity.getEmail() != null ? new Email(jpaEntity.getEmail()) : null;
        Password password = Password.fromEncoded(jpaEntity.getPassword());
        
        return new User(id, username, email, password, jpaEntity.getCreatedAt());
    }
    
    /**
     * Updates JPA entity with data from domain User (for updates).
     */
    public static void updateJpaEntity(UserJpaEntity jpaEntity, User domainUser) {
        jpaEntity.setUsername(domainUser.getUsername().getValue());
        jpaEntity.setPassword(domainUser.getPassword().getEncodedValue());
        jpaEntity.setEmail(domainUser.getEmail() != null ? domainUser.getEmail().getValue() : null);
        jpaEntity.setUpdatedAt(domainUser.getUpdatedAt());
    }
}
