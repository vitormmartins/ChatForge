package io.vitormmartins.chatforge.infrastructure.config;

import io.vitormmartins.chatforge.domain.user.exception.UsernameAlreadyExistsException;
import io.vitormmartins.chatforge.domain.user.exception.UserNotFoundException;
import io.vitormmartins.chatforge.domain.user.exception.InvalidPasswordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @Value("${errors.base-url:https://api.chatforge.io/errors}")
  private String errorBaseUrl;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidationExceptions(
          MethodArgumentNotValidException exception) {

    logger.warn("Validation error: {}", exception.getMessage());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Validation failed for one or more fields"
    );
    problemDetail.setType(URI.create(errorBaseUrl + "/validation"));
    problemDetail.setTitle("Validation Error");
    problemDetail.setProperty("timestamp", Instant.now());

    Map<String, String> errors = new HashMap<>();
    exception.getBindingResult().getFieldErrors()
            .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    problemDetail.setProperty("errors", errors);

    return ResponseEntity.badRequest().body(problemDetail);
  }

  @ExceptionHandler(UsernameAlreadyExistsException.class)
  public ResponseEntity<ProblemDetail> handleUsernameAlreadyExists(
          UsernameAlreadyExistsException exception) {

    logger.warn("Username already exists: {}", exception.getUsername());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            exception.getMessage()
    );
    problemDetail.setType(URI.create(errorBaseUrl + "/username-exists"));
    problemDetail.setTitle("Username Already Exists");
    problemDetail.setProperty("username", exception.getUsername());
    problemDetail.setProperty("timestamp", Instant.now());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleUserNotFound(
          UserNotFoundException exception) {

    logger.warn("User not found: {}", exception.getIdentifier());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            exception.getMessage()
    );
    problemDetail.setType(URI.create(errorBaseUrl + "/user-not-found"));
    problemDetail.setTitle("User Not Found");
    problemDetail.setProperty("identifier", exception.getIdentifier());
    problemDetail.setProperty("timestamp", Instant.now());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
  }

  @ExceptionHandler(InvalidPasswordException.class)
  public ResponseEntity<ProblemDetail> handleInvalidPassword(
          InvalidPasswordException exception) {

    logger.warn("Invalid password attempt");

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            exception.getMessage()
    );
    problemDetail.setType(URI.create(errorBaseUrl + "/invalid-password"));
    problemDetail.setTitle("Invalid Password");
    problemDetail.setProperty("timestamp", Instant.now());

    return ResponseEntity.badRequest().body(problemDetail);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ProblemDetail> handleIllegalArgument(
          IllegalArgumentException exception) {

    logger.warn("Illegal argument: {}", exception.getMessage());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            exception.getMessage()
    );
    problemDetail.setType(URI.create(errorBaseUrl + "/invalid-argument"));
    problemDetail.setTitle("Invalid Argument");
    problemDetail.setProperty("timestamp", Instant.now());

    return ResponseEntity.badRequest().body(problemDetail);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleGenericException(Exception exception) {
    logger.error("Unexpected error", exception);

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"
    );
    problemDetail.setType(URI.create(errorBaseUrl + "/internal-error"));
    problemDetail.setTitle("Internal Server Error");
    problemDetail.setProperty("timestamp", Instant.now());

    return ResponseEntity.internalServerError().body(problemDetail);
  }
}