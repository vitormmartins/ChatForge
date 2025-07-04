package io.vitormmartins.chatforge.application.user.dto;

/**
 * Command for user registration use case.
 */
public record RegisterUserCommand(
    String username,
    String email,
    String rawPassword
) {
    public RegisterUserCommand {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
    }
}
