package io.vitormmartins.chatforge.domain.user.model;

import java.util.Objects;

/**
 * Value object representing an encoded password.
 * This class assumes the password is already encoded/hashed.
 */
public record Password(String encodedValue) {
  public Password {
    if (encodedValue == null || encodedValue.trim().isEmpty()) {
      throw new IllegalArgumentException("Password cannot be null or empty");
    }
  }

  /**
   * Creates a password from a raw (plain text) value.
   * Note: This should only be used with pre-encoded passwords.
   * For raw passwords, use the application service with password encoder.
   */
  public static Password fromEncoded(String encodedValue) {
    return new Password(encodedValue);
  }

  /**
   * Checks if the provided raw password matches this encoded password.
   * Note: This is a placeholder - the actual implementation should use PasswordEncoder
   */
  public boolean matches(String rawPassword) {
    // This is a simplified check - in real implementation,
    // this would use a PasswordEncoder service
    throw new UnsupportedOperationException(
            "Password matching should be handled by infrastructure layer with PasswordEncoder"
    );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Password password = (Password) o;
    return Objects.equals(encodedValue, password.encodedValue);
  }

}
