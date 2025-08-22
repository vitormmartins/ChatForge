package io.vitormmartins.chatforge.application.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticateUserCommandTest {

    @Test
    @DisplayName("Valid command creates object")
    void validCommandCreatesObject() {
        AuthenticateUserCommand cmd = new AuthenticateUserCommand("user", "pass");
        assertEquals("user", cmd.username());
        assertEquals("pass", cmd.rawPassword());
    }

    @Test
    @DisplayName("Null username throws exception")
    void nullUsernameThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new AuthenticateUserCommand(null, "pass")
        );
        assertEquals("Username is required", ex.getMessage());
    }

    @Test
    @DisplayName("Empty username throws exception")
    void emptyUsernameThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new AuthenticateUserCommand("", "pass")
        );
        assertEquals("Username is required", ex.getMessage());
    }

    @Test
    @DisplayName("Blank username throws exception")
    void blankUsernameThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new AuthenticateUserCommand("   ", "pass")
        );
        assertEquals("Username is required", ex.getMessage());
    }

    @Test
    @DisplayName("Null password throws exception")
    void nullRawPasswordThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new AuthenticateUserCommand("user", null)
        );
        assertEquals("Password is required", ex.getMessage());
    }

    @Test
    @DisplayName("Empty password throws exception")
    void emptyRawPasswordThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new AuthenticateUserCommand("user", "")
        );
        assertEquals("Password is required", ex.getMessage());
    }

    @Test
    @DisplayName("Blank password throws exception")
    void blankRawPasswordThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new AuthenticateUserCommand("user", "   ")
        );
        assertEquals("Password is required", ex.getMessage());
    }
}