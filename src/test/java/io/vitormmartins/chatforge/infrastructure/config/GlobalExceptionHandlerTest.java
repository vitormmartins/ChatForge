package io.vitormmartins.chatforge.infrastructure.config;

import io.vitormmartins.chatforge.domain.user.exception.InvalidPasswordException;
import io.vitormmartins.chatforge.domain.user.exception.UserNotFoundException;
import io.vitormmartins.chatforge.domain.user.exception.UsernameAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() throws Exception {
    handler = new GlobalExceptionHandler();
    // definir errorBaseUrl via reflection para evitar null ao criar URIs
    Field f = GlobalExceptionHandler.class.getDeclaredField("errorBaseUrl");
    f.setAccessible(true);
    f.set(handler, "https://api.chatforge.io/errors");
  }

  // método dummy usado apenas para criar MethodParameter
  @SuppressWarnings("unused")
  private void dummyMethod(String arg) {
    // ...existing code...
  }

  @Test
  void handleValidationExceptions() throws NoSuchMethodException {
    Method method = this.getClass().getDeclaredMethod("dummyMethod", String.class);
    MethodParameter mp = new MethodParameter(method, 0);

    BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");
    bindingResult.addError(new FieldError("objectName", "name", "must not be blank"));

    MethodArgumentNotValidException exception = new MethodArgumentNotValidException(mp, bindingResult);

    ResponseEntity<ProblemDetail> response = handler.handleValidationExceptions(exception);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    ProblemDetail pd = response.getBody();
    assertNotNull(pd);
    assertEquals("Validation Error", pd.getTitle());
    assertTrue(pd.getType().toString().endsWith("/validation"));
    assertNotNull(pd.getProperties());
    assertNotNull(pd.getProperties().get("timestamp"));
    Object errorsObj = pd.getProperties().get("errors");
    assertInstanceOf(Map.class, errorsObj);
    Map<?, ?> errors = (Map<?, ?>) errorsObj;
    assertEquals("must not be blank", errors.get("name"));
  }

  @Test
  void handleUsernameAlreadyExists() {
    UsernameAlreadyExistsException ex = mock(UsernameAlreadyExistsException.class);
    when(ex.getUsername()).thenReturn("john");
    when(ex.getMessage()).thenReturn("username exists");

    ResponseEntity<ProblemDetail> response = handler.handleUsernameAlreadyExists(ex);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    ProblemDetail pd = response.getBody();
    assertNotNull(pd);
    assertEquals("Username Already Exists", pd.getTitle());
    assertTrue(pd.getType().toString().endsWith("/username-exists"));
    assertNotNull(pd.getProperties());
    assertEquals("john", pd.getProperties().get("username"));
    assertNotNull(pd.getProperties().get("timestamp"));
  }

  @Test
  void handleUserNotFound() {
    UserNotFoundException ex = mock(UserNotFoundException.class);
    when(ex.getIdentifier()).thenReturn("id-123");
    when(ex.getMessage()).thenReturn("user not found");

    ResponseEntity<ProblemDetail> response = handler.handleUserNotFound(ex);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    ProblemDetail pd = response.getBody();
    assertNotNull(pd);
    assertEquals("User Not Found", pd.getTitle());
    assertTrue(pd.getType().toString().endsWith("/user-not-found"));
    assertNotNull(pd.getProperties());
    assertNotNull(pd.getProperties().get("timestamp"));
  }

  @Test
  void handleInvalidPassword() {
    InvalidPasswordException ex = mock(InvalidPasswordException.class);
    when(ex.getMessage()).thenReturn("invalid password");

    ResponseEntity<ProblemDetail> response = handler.handleInvalidPassword(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    ProblemDetail pd = response.getBody();
    assertNotNull(pd);
    assertEquals("Invalid Password", pd.getTitle());
    assertTrue(pd.getType().toString().endsWith("/invalid-password"));
    assertNotNull(pd.getProperties());
    assertNotNull(pd.getProperties().get("timestamp"));
  }

  @Test
  void handleIllegalArgument() {
    IllegalArgumentException ex = new IllegalArgumentException("bad arg");

    ResponseEntity<ProblemDetail> response = handler.handleIllegalArgument(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    ProblemDetail pd = response.getBody();
    assertNotNull(pd);
    assertEquals("Invalid Argument", pd.getTitle());
    assertTrue(pd.getType().toString().endsWith("/invalid-argument"));
    assertEquals("bad arg", pd.getDetail());
    assertNotNull(pd.getProperties());
    assertNotNull(pd.getProperties().get("timestamp"));
  }

  @Test
  void handleGenericException() {
    Exception ex = new Exception("boom");

    ResponseEntity<ProblemDetail> response = handler.handleGenericException(ex);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    ProblemDetail pd = response.getBody();
    assertNotNull(pd);
    assertEquals("Internal Server Error", pd.getTitle());
    assertTrue(pd.getType().toString().endsWith("/internal-error"));
    assertNotNull(pd.getProperties());
    assertNotNull(pd.getProperties().get("timestamp"));
  }
}