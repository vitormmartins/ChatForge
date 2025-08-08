package io.vitormmartins.chatforge.web.controller;

import io.vitormmartins.chatforge.application.user.dto.UserDto;
import io.vitormmartins.chatforge.application.user.service.UserApplicationService;
import io.vitormmartins.chatforge.infrastructure.security.util.JwtUtil;
import io.vitormmartins.chatforge.web.controller.dto.LoginRequest;
import io.vitormmartins.chatforge.web.controller.dto.RegisterRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

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
  private final String testEmail = "test@example.com";
  private final String testToken = "test.jwt.token";

  @Test
  void login_ShouldReturnToken_WhenCredentialsAreValid() {
    // Arrange
    LoginRequest request = new LoginRequest(testUsername, testPassword);
    when(jwtUtil.generateToken(testUsername)).thenReturn(testToken);

    // Mock authentication
    Authentication auth = mock(Authentication.class);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(auth);

    // Act
    ResponseEntity<String> response = authController.login(request);

    // Assert
    assertEquals(testToken, response.getBody());
    verify(authenticationManager).authenticate(
            new UsernamePasswordAuthenticationToken(testUsername, testPassword));
    verify(jwtUtil).generateToken(testUsername);
  }

  @Test
  void loginWithCookie_ShouldSetCookie_WhenCredentialsAreValid() {
    // Arrange
    LoginRequest request = new LoginRequest(testUsername, testPassword);
    when(jwtUtil.generateToken(testUsername)).thenReturn(testToken);

    // Mock authentication
    Authentication auth = mock(Authentication.class);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(auth);

    // Act
    ResponseEntity<String> response = authController.loginWithCookie(request, httpServletResponse);

    // Assert
    assertEquals("Authentication successful", response.getBody());
    verify(httpServletResponse).addCookie(any(Cookie.class));
    verify(authenticationManager).authenticate(
            new UsernamePasswordAuthenticationToken(testUsername, testPassword));
    verify(jwtUtil).generateToken(testUsername);
  }

  @Test
  void register_ShouldReturnUserDto_WhenRegistrationIsSuccessful() {
    // Arrange
    RegisterRequest request = new RegisterRequest(testUsername, testEmail, testPassword);
    UserDto expectedUserDto = new UserDto(
            1L,
            testUsername,
            testEmail,
            LocalDateTime.now()
    );

    when(userApplicationService.registerUser(any())).thenReturn(expectedUserDto);

    // Act
    ResponseEntity<UserDto> response = authController.register(request);

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