package io.vitormmartins.chatforge.application.user.dto;

import io.vitormmartins.chatforge.domain.user.model.User;

import java.time.LocalDateTime;

/**
 * Response DTO for user data.
 * Decouples application layer from generated API DTOs.
 */
public record UserResponse(
        Long id,
        String username,
        String email,
        LocalDateTime createdAt
) {
  public static UserResponse from(User user) {
    return new UserResponse(
            user.id().orElse(null),
            user.username().value(), // Se usar Value Object
            user.email() != null ? user.email().value() : null,
            user.createdAt()
    );
  }
}