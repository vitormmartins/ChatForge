package io.vitormmartins.chatforge.infrastructure.persistence.user.adapter;

import io.vitormmartins.chatforge.domain.user.model.User;
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
        
        if (!user.getId().isPresent()) {
            // New user - convert to JPA entity
            jpaEntity = UserMapper.toJpaEntity(user);
        } else {
            // Existing user - load and update
            jpaEntity = jpaRepository.findById(user.getId().get())
                                     .orElseThrow(() -> new IllegalArgumentException("User not found: "
                                                                                     + user.getId()));
            UserMapper.updateJpaEntity(jpaEntity, user);
        }
        
        UserJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return UserMapper.toDomainEntity(savedEntity);
    }
    
    @Override
    public Optional<User> findById(long id) {
        return jpaRepository.findById(id)
            .map(UserMapper::toDomainEntity);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
            .map(UserMapper::toDomainEntity);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }
    
    @Override
    public void deleteById(long id) {
        jpaRepository.deleteById(id);
    }
}
