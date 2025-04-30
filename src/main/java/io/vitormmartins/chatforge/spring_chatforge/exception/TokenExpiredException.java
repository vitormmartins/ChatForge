package io.vitormmartins.chatforge.spring_chatforge.exception;

public class TokenExpiredException extends RuntimeException {
  public TokenExpiredException(String message) {
    super(message);
  }
}
