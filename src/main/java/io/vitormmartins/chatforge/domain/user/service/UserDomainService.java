package io.vitormmartins.chatforge.domain.user.service;

import io.vitormmartins.chatforge.domain.user.model.Email;
import io.vitormmartins.chatforge.domain.user.model.Password;
import io.vitormmartins.chatforge.domain.user.model.User;
import io.vitormmartins.chatforge.domain.user.model.Username;
import io.vitormmartins.chatforge.domain.user.repository.UserRepository;

/**
 * Domain service for user-related business operations.
 * Contains business logic that doesn't naturally fit into a single entity.
 */
public record UserDomainService(UserRepository userRepository) {

  /**
   * Creates a new user ensuring business rules are satisfied.
   *
   * @param username the desired username
   * @param email    the user's email (optional)
   * @param password the encoded password
   * @return the created user
   * @throws IllegalArgumentException if a username already exists
   */
  public User createUser(Username username, Email email, Password password) {
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("Username already exists: " + username);
    }

    User user = User.create(username, email, password);
    return userRepository.save(user);
  }

  /**
   * Checks if a user can be created with the given username.
   *
   * @param username the username to check
   * @return true if a username is available
   */
  public boolean isUsernameAvailable(Username username) {
    return !userRepository.existsByUsername(username);
  }
}
