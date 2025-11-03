package io.vitormmartins.chatforge.domain.user.exception;

import java.io.Serial;

public class InvalidPasswordException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  public InvalidPasswordException() {
    super("Invalid password");
  }

  public InvalidPasswordException(String message) {
    super(message);
  }

  public InvalidPasswordException(String message, Throwable cause) {
    super(message, cause);
  }
}