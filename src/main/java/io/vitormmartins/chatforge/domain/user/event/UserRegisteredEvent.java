package io.vitormmartins.chatforge.domain.user.event;

import java.time.LocalDateTime;

/**
 * Domain event fired when a user is registered.
 */
public record UserRegisteredEvent(
        Long userId,
        String username,
        String email,
        LocalDateTime occurredAt
) {
  public UserRegisteredEvent(Long userId, String username, String email) {
    this(userId, username, email, LocalDateTime.now());
  }
}