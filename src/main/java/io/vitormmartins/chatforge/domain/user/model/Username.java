package io.vitormmartins.chatforge.domain.user.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing a username with validation rules.
 */
public record Username(String value) {
  private static final Pattern VALID_USERNAME = Pattern.compile("^\\w{3,20}$");

  public Username(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }

    String trimmed = value.trim();
    if (!VALID_USERNAME.matcher(trimmed).matches()) {
      throw new IllegalArgumentException(
              "Username must be 3-20 characters long and contain only letters, numbers, and underscores"
      );
    }

    this.value = trimmed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Username username = (Username) o;
    return Objects.equals(value, username.value);
  }

  @Override
  public String toString() {
    return value;
  }
}
