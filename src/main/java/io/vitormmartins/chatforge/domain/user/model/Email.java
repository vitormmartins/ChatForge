package io.vitormmartins.chatforge.domain.user.model;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing an email address with validation.
 */
public record Email(String value) {
  private static final Pattern VALID_EMAIL = Pattern.compile(
          "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
  );

  public Email(String value) {
    if (value != null && !value.trim().isEmpty()) {
      String trimmed = value.trim().toLowerCase();
      if (!VALID_EMAIL.matcher(trimmed).matches()) {
        throw new IllegalArgumentException("Invalid email format");
      }
      this.value = trimmed;
    } else {
      this.value = null; // Email is optional
    }
  }

  public boolean isPresent() {
    return value != null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Email email = (Email) o;
    return Objects.equals(value, email.value);
  }

  @Override
  @NotNull
  public String toString() {
    return value;
  }
}
