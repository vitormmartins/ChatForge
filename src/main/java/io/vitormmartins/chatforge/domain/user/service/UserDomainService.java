package io.vitormmartins.chatforge.domain.user.service;

import java.time.LocalDateTime;
import java.util.Optional;

import io.vitormmartins.chatforge.domain.user.model.User;
import io.vitormmartins.chatforge.domain.user.repository.UserRepository;

/**
 * Domain service for user-related business operations.
 * Contains business logic that doesn't naturally fit into a single entity.
 */
public class UserDomainService {
    
    private final UserRepository userRepository;
    
    public UserDomainService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Creates a new user ensuring business rules are satisfied.
     * @param username the desired username
     * @param email the user's email (optional)
     * @param password the encoded password
     * @return the created user
     * @throws IllegalArgumentException if username already exists
     */
    public User createUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        
        User user = new User(Optional.empty(), username, email, password, LocalDateTime.now());
        return userRepository.save(user);
    }
    
    /**
     * Checks if a user can be created with the given username.
     * @param username the username to check
     * @return true if username is available
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
}
