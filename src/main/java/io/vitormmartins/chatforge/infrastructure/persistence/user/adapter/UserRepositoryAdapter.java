package io.vitormmartins.chatforge.infrastructure.persistence.user.adapter;

import io.vitormmartins.chatforge.domain.user.model.User;
import io.vitormmartins.chatforge.domain.user.repository.UserRepository;
import io.vitormmartins.chatforge.infrastructure.persistence.user.entity.UserJpaEntity;
import io.vitormmartins.chatforge.infrastructure.persistence.user.mapper.UserMapper;
import io.vitormmartins.chatforge.infrastructure.persistence.user.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Adapter implementing UserRepository port using Spring Data JPA.
 * Bridges domain layer with persistence infrastructure.
 */
@Repository
@Transactional
public class UserRepositoryAdapter implements UserRepository {
    
    private final UserJpaRepository jpaRepository;
    
    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public User save(User user) {
        UserJpaEntity jpaEntity = user.id().isPresent()
            ? updateExistingEntity(user)
            : createNewEntity(user);
        
        UserJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return UserMapper.toDomainEntity(savedEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(long id) {
        return jpaRepository.findById(id)
            .map(UserMapper::toDomainEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
            .map(UserMapper::toDomainEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }
    
    @Override
    public void deleteById(long id) {
        jpaRepository.deleteById(id);
    }
    
    // Helper methods
    private UserJpaEntity createNewEntity(User user) {
        return UserMapper.toJpaEntity(user);
    }
    
    private UserJpaEntity updateExistingEntity(User user) {
        UserJpaEntity jpaEntity = jpaRepository.findById(user.id().get())
            .orElseThrow(() -> new IllegalArgumentException(
                "User not found for update: " + user.id().get()
            ));
        UserMapper.updateJpaEntity(jpaEntity, user);
        return jpaEntity;
    }
}