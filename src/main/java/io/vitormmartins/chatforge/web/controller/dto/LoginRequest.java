package io.vitormmartins.chatforge.web.controller.dto;

/**
 * Request DTO for user login.
 */
public record LoginRequest(
    String username,
    String password
) {}
