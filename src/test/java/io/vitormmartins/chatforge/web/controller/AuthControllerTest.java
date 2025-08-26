package io.vitormmartins.chatforge.web.controller;

import io.vitormmartins.chatforge.application.user.service.UserApplicationService;
import io.vitormmartins.chatforge.generated.model.LoginRequest;
import io.vitormmartins.chatforge.generated.model.RegisterRequest;
import io.vitormmartins.chatforge.generated.model.UserDto;
import io.vitormmartins.chatforge.infrastructure.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private UserApplicationService userApplicationService;

  @Mock
  private HttpServletResponse httpServletResponse;

  @InjectMocks
  private AuthController authController;

  private final String testUsername = "testuser";
  private final String testPassword = "password123";
  private final String testToken = "test.jwt.token";

  @Test
  void v1AuthLoginPost_ShouldReturnToken_WhenCredentialsAreValid() {
    // Arrange
    @Valid LoginRequest request = new LoginRequest(testUsername, testPassword);
    when(jwtUtil.generateToken(testUsername)).thenReturn(testToken);

    // Mock authentication
    Authentication auth = mock(Authentication.class);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(auth);

    // Act
    ResponseEntity<String> response = authController.v1AuthLoginPost(request);

    // Assert
    assertEquals(testToken, response.getBody());
    verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(testUsername, testPassword));
    verify(jwtUtil).generateToken(testUsername);
  }

  @Test
  void v1AuthLoginPostWithCookie_ShouldSetCookie_WhenCredentialsAreValid() {
    // Arrange
    @Valid LoginRequest request = new LoginRequest(testUsername, testPassword);
    when(jwtUtil.generateToken(testUsername)).thenReturn(testToken);

    // Mock authentication
    Authentication auth = mock(Authentication.class);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(auth);

    // Act
    ResponseEntity<String> response = authController.v1AuthLoginCookiePost(request);

    // Assert
    assertEquals("Authentication successful", response.getBody());
    // Verify that a cookie is set in the response header instead of interacting with httpServletResponse
    assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
    verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(testUsername, testPassword));
    verify(jwtUtil).generateToken(testUsername);
  }

  @Test
  void v1AuthRegisterPost_ShouldReturnUserDto_WhenRegistrationIsSuccessful() {
    // Arrange
    String testEmail = "test@example.com";
    @Valid RegisterRequest request = new RegisterRequest(testUsername, testEmail, testPassword);

    UserDto expectedUserDto = new UserDto();
    expectedUserDto.setId(1L);
    expectedUserDto.setUsername(testUsername);
    expectedUserDto.setEmail(testEmail);
    expectedUserDto.setCreatedAt(OffsetDateTime.now());

    when(userApplicationService.registerUser(any())).thenReturn(expectedUserDto);

    // Act
    ResponseEntity<UserDto> response = authController.v1AuthRegisterPost(request);

    // Assert
    assertEquals(expectedUserDto, response.getBody());
    verify(userApplicationService).registerUser(any());
  }

  @Test
  void authenticateAndGenerateToken_ShouldReturnToken_WhenAuthenticationSucceeds() {
    // Arrange
    when(jwtUtil.generateToken(testUsername)).thenReturn(testToken);

    // Mock authentication
    Authentication auth = mock(Authentication.class);
    when(authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(testUsername, testPassword)))
            .thenReturn(auth);

    // Act
    String token = authController.authenticateAndGenerateToken(testUsername, testPassword);

    // Assert
    assertEquals(testToken, token);
    verify(authenticationManager).authenticate(
            new UsernamePasswordAuthenticationToken(testUsername, testPassword));
    verify(jwtUtil).generateToken(testUsername);
  }
}