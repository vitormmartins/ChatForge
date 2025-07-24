package io.vitormmartins.chatforge.web.controller.dto;

/**
 * Represents the data transfer object for a user registration request.
 * This record is used to capture the username, email, and password from the registration form
 * and pass it to the controller and application services.
 *
 * @param username the username chosen by the user
 * @param email the user's email address
 * @param password the user's chosen password
 */
public record RegisterRequest(
    String username,
    String email,
    String password
) {}
