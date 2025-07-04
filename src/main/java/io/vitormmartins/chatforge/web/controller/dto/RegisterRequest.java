package io.vitormmartins.chatforge.web.controller.dto;

/**
 * Request DTO for user registration.
 */
public record RegisterRequest(
    String username,
    String email,
    String password
) {}
