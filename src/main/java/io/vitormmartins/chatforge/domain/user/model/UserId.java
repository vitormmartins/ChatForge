package io.vitormmartins.chatforge.domain.user.model;

import java.util.Objects;

/**
 * Value object representing a user identifier.
 */
public record UserId(Long value) {
  public UserId {
    if (value != null && value <= 0) {
      throw new IllegalArgumentException("User ID must be positive");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserId userId = (UserId) o;
    return Objects.equals(value, userId.value);
  }

  @Override
  public String toString() {
    return "UserId{" + value + '}';
  }
}
