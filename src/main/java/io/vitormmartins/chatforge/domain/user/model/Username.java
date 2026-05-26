package io.vitormmartins.chatforge.domain.user.model;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * Username value object - immutable.
 */
public record Username(String value) {
  private static final int MIN_LENGTH = 3;
  private static final int MAX_LENGTH = 50;

  public Username {
    Objects.requireNonNull(value, "Username cannot be null");
    String trimmed = value.trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be empty");
    }
    if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
      throw new IllegalArgumentException(
              String.format("Username must be between %d and %d characters", MIN_LENGTH, MAX_LENGTH)
      );
    }
    value = trimmed; // Reassign to trimmed value
  }

  @Override
  @NotNull
  public String toString() {
    return value;
  }
}