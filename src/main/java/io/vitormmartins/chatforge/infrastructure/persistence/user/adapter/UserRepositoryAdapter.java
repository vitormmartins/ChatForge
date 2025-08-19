package io.vitormmartins.chatforge.infrastructure.persistence.user.adapter;

import io.vitormmartins.chatforge.domain.user.model.User;
import io.vitormmartins.chatforge.domain.user.model.UserId;
import io.vitormmartins.chatforge.domain.user.model.Username;
import io.vitormmartins.chatforge.domain.user.repository.UserRepository;
import io.vitormmartins.chatforge.infrastructure.persistence.user.entity.UserJpaEntity;
import io.vitormmartins.chatforge.infrastructure.persistence.user.mapper.UserMapper;
import io.vitormmartins.chatforge.infrastructure.persistence.user.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter that implements the UserRepository port using Spring Data JPA.
 * This bridges the domain layer with the persistence infrastructure.
 */
@Component
public class UserRepositoryAdapter implements UserRepository {
    
    private final UserJpaRepository jpaRepository;
    
    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public User save(User user) {
        UserJpaEntity jpaEntity;
        
        if (user.getId() == null) {
            // New user - convert to JPA entity
            jpaEntity = UserMapper.toJpaEntity(user);
        } else {
            // Existing user - load and update
            jpaEntity = jpaRepository.findById(user.getId().value())
                                     .orElseThrow(() -> new IllegalArgumentException("User not found: "
                                                                                     + user.getId()));
            UserMapper.updateJpaEntity(jpaEntity, user);
        }
        
        UserJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return UserMapper.toDomainEntity(savedEntity);
    }
    
    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value())
            .map(UserMapper::toDomainEntity);
    }
    
    @Override
    public Optional<User> findByUsername(Username username) {
        return jpaRepository.findByUsername(username.value())
            .map(UserMapper::toDomainEntity);
    }
    
    @Override
    public boolean existsByUsername(Username username) {
        return jpaRepository.existsByUsername(username.value());
    }
    
    @Override
    public void deleteById(UserId id) {
        jpaRepository.deleteById(id.value());
    }
}
