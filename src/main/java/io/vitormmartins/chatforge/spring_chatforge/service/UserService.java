package io.vitormmartins.chatforge.spring_chatforge.service;

public interface UserService {
  boolean isUsernameExists(String username);

  /**
   * Creates a new user in the system. The provided user object should contain
   * all necessary user details. The username must be unique and not already
   * exist in the system.
   *
   * @param username the unique identifier for the new user
   * @param password the user's password
   * @param email    the user's email address
   */
  void createUser(String username, String password, String email);
}
