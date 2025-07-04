package io.vitormmartins.chatforge.application.user.dto;

import java.time.LocalDateTime;

/**
 * Result of user registration use case.
 */
public record UserDto(
    Long id,
    String username,
    String email,
    LocalDateTime createdAt
) {}
