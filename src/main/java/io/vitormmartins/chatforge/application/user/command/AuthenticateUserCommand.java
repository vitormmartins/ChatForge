package io.vitormmartins.chatforge.application.user.command;

/**
 * Command for user authentication use case.
 */
public record AuthenticateUserCommand(
    String username,
    String rawPassword
) {
    public AuthenticateUserCommand {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
    }
}
