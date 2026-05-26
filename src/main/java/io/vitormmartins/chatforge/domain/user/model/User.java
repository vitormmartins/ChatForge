package io.vitormmartins.chatforge.domain.user.model;

import io.vitormmartins.chatforge.application.user.service.UserApplicationService;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * User domain entity - immutable aggregate root.
 * Represents a user in the chat system following DDD principles.
 */
public record User(
        Optional<Long> id,
        Username username,
        Email email,
        String password,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
  // Constructor for new users (without ID)
  public User(Username username, Email email, String password) {
    this(Optional.empty(), username, email, password, LocalDateTime.now(), LocalDateTime.now());
  }

  // Compact canonical constructor for validation
  public User {
    validateUsername(username);
    validatePassword(password);
    Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
    Objects.requireNonNull(updatedAt, "UpdatedAt cannot be null");

  }

  public static User createNew(Username username, Email email, String password) {
    User user = new User(username, email, password);
    UserApplicationService.publishUserCreatedEvent(user); // Publish domain event
    return user;
  }

  // Business methods returning new instances (immutability)
  public User updatePassword(String newEncodedPassword) {
    validatePassword(newEncodedPassword);
    if (this.password.equals(newEncodedPassword)) {
      throw new IllegalArgumentException("New password must be different from current password");
    }
    return new User(
            this.id,
            this.username,
            this.email,
            newEncodedPassword,
            this.createdAt,
            LocalDateTime.now()
    );
  }

  public User updateEmail(Email newEmail) {
    if (this.email != null && this.email.equals(newEmail)) {
      throw new IllegalArgumentException("New email must be different from current email");
    }
    return new User(
            this.id,
            this.username,
            newEmail,
            this.password,
            this.createdAt,
            LocalDateTime.now()
    );
  }

  // Assign ID after persistence (for new users)
  public User withId(Long id) {
    if (this.id != null && this.id.isPresent()) {
      throw new IllegalStateException("Cannot reassign ID to existing user");
    }
    return new User(Optional.ofNullable(id), this.username, this.email, this.password, this.createdAt, this.updatedAt);
  }

  // Domain validations
  private void validateUsername(Username username) {
    Objects.requireNonNull(username, "Username cannot be null");
    if (username.value().trim().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be empty");
    }
    if (username.value().length() < 3 || username.value().length() > 50) {
      throw new IllegalArgumentException("Username must be between 3 and 50 characters");
    }
  }

  private void validatePassword(String password) {
    Objects.requireNonNull(password, "Password cannot be null");
    if (password.trim().isEmpty()) {
      throw new IllegalArgumentException("Password cannot be empty");
    }
  }

  @NotNull
  @Override
  public String toString() {
    return "User{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            '}';
  }
}