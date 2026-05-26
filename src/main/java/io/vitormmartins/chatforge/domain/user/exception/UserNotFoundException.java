package io.vitormmartins.chatforge.domain.user.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class UserNotFoundException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;
  private final String identifier;

  public UserNotFoundException(String identifier) {
    super("User not found: " + identifier);
    this.identifier = identifier;
  }

  public UserNotFoundException(String identifier, String message) {
    super(message);
    this.identifier = identifier;
  }

}