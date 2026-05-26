package io.vitormmartins.chatforge.web.controller;

import io.vitormmartins.chatforge.application.user.dto.UserResponse;
import io.vitormmartins.chatforge.application.user.service.UserApplicationService;
import io.vitormmartins.chatforge.generated.model.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock
  private UserApplicationService userApplicationService;

  @InjectMocks
  private UserController userController;

  private final String testUsername = "testuser";
  private final LocalDateTime testLocalCreatedAt = LocalDateTime.now();
  private final OffsetDateTime testCreatedAt = testLocalCreatedAt.atOffset(java.time.ZoneOffset.systemDefault()
                                                                           .getRules()
                                                                           .getOffset(testLocalCreatedAt));

  @Test
  void getUserByUsername_ShouldReturnUser_WhenUserExists() {
    // Arrange
    UserResponse userResponse = new UserResponse(1L,
            testUsername,
            "test@example.com",
            testLocalCreatedAt);

    when(userApplicationService.findUserByUsername(testUsername))
            .thenReturn(Optional.of(userResponse));

    UserDto expectedUser = new UserDto();
    Long testUserId = 1L;
    expectedUser.setId(testUserId);
    expectedUser.setUsername(testUsername);
    String testEmail = "test@example.com";
    expectedUser.setEmail(testEmail);
    expectedUser.setCreatedAt(testCreatedAt);


    // Act
    ResponseEntity<UserDto> response = userController.getUserByUsername(testUsername);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.hasBody());
    assertEquals(expectedUser, response.getBody());
    verify(userApplicationService).findUserByUsername(testUsername);
  }

  @Test
  void getUserByUsername_ShouldReturnNotFound_WhenUserDoesNotExist() {
    // Arrange
    when(userApplicationService.findUserByUsername(testUsername))
            .thenReturn(Optional.empty());

    // Act
    ResponseEntity<UserDto> response = userController.getUserByUsername(testUsername);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertFalse(response.hasBody());
    verify(userApplicationService).findUserByUsername(testUsername);
  }

  @Test
  void checkUsernameAvailability_ShouldReturnTrue_WhenUsernameIsAvailable() {
    // Arrange
    when(userApplicationService.isUsernameAvailable(testUsername))
            .thenReturn(true);

    // Act
    ResponseEntity<Boolean> response = userController.checkUsernameAvailability(testUsername);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.hasBody());
    assertEquals(Boolean.TRUE, response.getBody());
    verify(userApplicationService).isUsernameAvailable(testUsername);
  }

  @Test
  void checkUsernameAvailability_ShouldReturnFalse_WhenUsernameIsTaken() {
    // Arrange
    when(userApplicationService.isUsernameAvailable(testUsername))
            .thenReturn(false);

    // Act
    ResponseEntity<Boolean> response = userController.checkUsernameAvailability(testUsername);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.hasBody());
    assertEquals(Boolean.FALSE, response.getBody());
    verify(userApplicationService).isUsernameAvailable(testUsername);
  }

  @Test
  void checkUsernameAvailability_ShouldHandleEmptyUsername() {
    // Arrange
    String emptyUsername = "";
    when(userApplicationService.isUsernameAvailable(emptyUsername))
            .thenReturn(true);

    // Act
    ResponseEntity<Boolean> response = userController.checkUsernameAvailability(emptyUsername);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.hasBody());
    assertEquals(Boolean.TRUE, response.getBody());
    verify(userApplicationService).isUsernameAvailable(emptyUsername);
  }
}