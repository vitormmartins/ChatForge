package io.vitormmartins.chatforge.spring_chatforge.exception;

public class InvalidTokenException extends RuntimeException {
  public InvalidTokenException(String message) {
    super(message);
  }
}
