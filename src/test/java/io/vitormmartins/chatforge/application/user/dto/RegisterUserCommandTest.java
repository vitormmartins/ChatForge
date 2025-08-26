package io.vitormmartins.chatforge.application.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterUserCommandTest {

    @Test
    @DisplayName("Valid RegisterUserCommand creates object")
    void validCommandCreatesObject() {
        RegisterUserCommand cmd = new RegisterUserCommand("user", "user@example.com", "password123");
        assertEquals("user", cmd.username());
        assertEquals("user@example.com", cmd.email());
        assertEquals("password123", cmd.rawPassword());
    }

    @Test
    @DisplayName("Null username throws exception")
    void nullUsernameThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            new RegisterUserCommand(null, "user@example.com", "password123")
        );
        assertEquals("Username is required", ex.getMessage());
    }

    @Test
    @DisplayName("Empty username throws exception")
    void emptyUsernameThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            new RegisterUserCommand("", "user@example.com", "password123")
        );
        assertEquals("Username is required", ex.getMessage());
    }

    @Test
    @DisplayName("Blank username throws exception")
    void blankUsernameThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            new RegisterUserCommand("   ", "user@example.com", "password123")
        );
        assertEquals("Username is required", ex.getMessage());
    }

    @Test
    @DisplayName("Null password throws exception")
    void nullPasswordThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            new RegisterUserCommand("user", "user@example.com", null)
        );
        assertEquals("Password is required", ex.getMessage());
    }

    @Test
    @DisplayName("Empty password throws exception")
    void emptyPasswordThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            new RegisterUserCommand("user", "user@example.com", "")
        );
        assertEquals("Password is required", ex.getMessage());
    }

    @Test
    @DisplayName("Blank password throws exception")
    void blankPasswordThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            new RegisterUserCommand("user", "user@example.com", "   ")
        );
        assertEquals("Password is required", ex.getMessage());
    }

    @Test
    @DisplayName("Email accepts null without exception")
    void nullEmailIsAllowed() {
        // Even though email is null, our validation only applies to username and rawPassword.
        RegisterUserCommand cmd = new RegisterUserCommand("user", null, "password123");
        assertEquals("user", cmd.username());
        assertNull(cmd.email());
        assertEquals("password123", cmd.rawPassword());
    }
}