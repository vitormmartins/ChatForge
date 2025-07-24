package io.vitormmartins.chatforge.web.controller.dto;

/**
 * Represents the data transfer object for a user login request.
 * This record is used to capture the username and password from the login form
 * and pass it to the controller for authentication.
 *
 * @param username the username of the user trying to log in
 * @param password the password of the user
 */
public record LoginRequest(
    String username,
    String password
) {}
