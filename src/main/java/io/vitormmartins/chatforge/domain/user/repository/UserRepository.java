package io.vitormmartins.chatforge.domain.user.repository;

import io.vitormmartins.chatforge.domain.user.model.User;
import io.vitormmartins.chatforge.domain.user.model.UserId;
import io.vitormmartins.chatforge.domain.user.model.Username;

import java.util.Optional;

/**
 * Repository interface for User domain entity.
 * This is a port (interface) that will be implemented by the infrastructure layer.
 */
public interface UserRepository {
    
    /**
     * Saves a user entity.
     * @param user the user to save
     * @return the saved user with generated ID (if new)
     */
    User save(User user);
    
    /**
     * Finds a user by their unique identifier.
     * @param id the user ID
     * @return optional user if found
     */
    Optional<User> findById(UserId id);
    
    /**
     * Finds a user by their username.
     * @param username the username to search for
     * @return optional user if found
     */
    Optional<User> findByUsername(Username username);
    
    /**
     * Checks if a username already exists.
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(Username username);
    
    /**
     * Deletes a user by their ID.
     * @param id the user ID to delete
     */
    void deleteById(UserId id);
}
