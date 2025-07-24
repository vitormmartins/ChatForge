package io.vitormmartins.chatforge.infrastructure.security.exception;

public class TokenExpiredException extends RuntimeException {
  public TokenExpiredException(String message) {
    super(message);
  }
}
