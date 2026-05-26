package io.vitormmartins.chatforge.domain.user.exception;

import lombok.Getter;

/**
 * Exception thrown when a username already exists.
 */
@Getter
public class UsernameAlreadyExistsException extends RuntimeException {
    private final String username;

    public UsernameAlreadyExistsException(String username) {
        super("Username already exists: " + username);
        this.username = username;
    }

}
