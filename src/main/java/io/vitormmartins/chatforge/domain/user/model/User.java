package io.vitormmartins.chatforge.domain.user.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * User domain entity representing a user in the chat system.
 * This is the core domain model independent of any infrastructure concerns.
 */
@Getter
public class User {
  private final UserId id;
  private final Username username;
  private final Email email;
  private final Password password;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public User(UserId id, Username username, Email email, Password password, LocalDateTime createdAt) {
    this.id = id;
    this.username = Objects.requireNonNull(username, "Username cannot be null");
    this.email = email;
    this.password = Objects.requireNonNull(password, "Password cannot be null");
    this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
    this.updatedAt = createdAt;
  }

  // Factory method for creating new users
  public static User create(Username username, Email email, Password password) {
    return new User(null, username, email, password, LocalDateTime.now());
  }

  // Business methods
  public void updatePassword(Password newPassword) {
    if (this.password.equals(newPassword)) {
      throw new IllegalArgumentException("New password must be different from current password");
    }
    // Note: In real implementation, this would return a new instance (immutable)
    this.updatedAt = LocalDateTime.now();
  }

  public boolean isValidPassword(String rawPassword) {
    return password.matches(rawPassword);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
