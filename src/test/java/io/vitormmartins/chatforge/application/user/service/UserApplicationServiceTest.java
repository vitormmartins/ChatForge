package io.vitormmartins.chatforge.application.user.service;

import io.vitormmartins.chatforge.application.user.command.AuthenticateUserCommand;
import io.vitormmartins.chatforge.application.user.command.RegisterUserCommand;
import io.vitormmartins.chatforge.application.user.dto.UserResponse;
import io.vitormmartins.chatforge.domain.user.model.*;
import io.vitormmartins.chatforge.domain.user.repository.UserRepository;
import io.vitormmartins.chatforge.domain.user.service.UserDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserApplicationServiceTest {

  private UserRepository userRepository;
  private UserDomainService userDomainService;
  private PasswordEncoderPort passwordEncoder;
  private UserApplicationService service;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    userDomainService = mock(UserDomainService.class);
    passwordEncoder = mock(PasswordEncoderPort.class);
    service = new UserApplicationService(userRepository, userDomainService, passwordEncoder);
  }

  @Test
  @DisplayName("registerUser: Valid command returns mapped UserDto")
  void testRegisterUser() {
    // Arrange
    RegisterUserCommand command = new RegisterUserCommand("user", "user@example.com", "pass");
    when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

    // Create a fake User instance with stubbing for mapping
    User dummyUser = mock(User.class);
    // Properly mock Long instead of casting
    Long dummyId = 1L;
    when(dummyUser.id()).thenReturn(dummyId.describeConstable());

    when(dummyUser.username()).thenReturn(new Username("user"));
    when(dummyUser.email()).thenReturn(new Email("user@example.com"));
    LocalDateTime fixedDate = LocalDateTime.of( 2025,
                                                  7,
                                                  23,
                                                  21,
                                                  28,
                                                  55,
                                                  0);
    when(dummyUser.createdAt()).thenReturn(fixedDate);
    when(userDomainService.createUser(
            ArgumentMatchers.any(Username.class),
            ArgumentMatchers.any(Email.class),
            ArgumentMatchers.any(String.class))
    ).thenReturn(dummyUser);

    // Act
    UserResponse dto = service.registerUser(command);

    // Assert
    assertNotNull(dto);
    assertEquals(1, dto.id());
    assertEquals("user", dto.username());
    assertEquals("user@example.com", dto.email());
    assertEquals(fixedDate, dto.createdAt());
  }

  @Test
  @DisplayName("authenticateUser: Valid credentials returns UserDto")
  void testAuthenticateUserSuccess() {
    // Arrange
    AuthenticateUserCommand command = new AuthenticateUserCommand("user", "pass");
    User dummyUser = mock(User.class);

    // Properly mock Long instead of casting
    Long dummyId = 2L;
    when(dummyUser.id()).thenReturn(dummyId.describeConstable());

    when(dummyUser.username()).thenReturn(new Username("user"));
    when(dummyUser.email()).thenReturn(new Email("user@example.com"));
    LocalDateTime fixedDate = LocalDateTime.of( 2025,
                                                  7,
                                                  24,
                                                  10,
                                                  0,
                                                  0,
                                                  0);
    when(dummyUser.createdAt()).thenReturn(fixedDate);
    // Stub password checking
    // Simulates that a user's password encoded value is "encodedPass"
    // To simulate, we assume dummyUser.getPassword() returns dummyPassword.
    when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(dummyUser));
    when(passwordEncoder.matches(eq("pass"), any())).thenReturn(true);

    // Act
    Optional<UserResponse> resultOpt = service.authenticateUser(command);

    // Assert
    assertTrue(resultOpt.isPresent());
    UserResponse dto = resultOpt.get();
    assertEquals(2, dto.id());
    assertEquals("user", dto.username());
    assertEquals("user@example.com", dto.email());
    assertEquals(fixedDate, dto.createdAt());
  }

  @Test
  @DisplayName("authenticateUser: Wrong password returns empty")
  void testAuthenticateUserFailure() {
    // Arrange
    AuthenticateUserCommand command = new AuthenticateUserCommand("user", "wrongpass");
    User dummyUser = mock(User.class);

    // Properly mock Long instead of casting
    Long dummyId = 3L;
    when(dummyUser.id()).thenReturn(dummyId.describeConstable());

    when(dummyUser.username()).thenReturn(new Username("user"));
    when(dummyUser.email()).thenReturn(new Email("user@example.com"));
    LocalDateTime fixedDate = LocalDateTime.now();
    when(dummyUser.createdAt()).thenReturn(fixedDate);
    when(dummyUser.password()).thenReturn("pass");
    when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(dummyUser));

    // Act
    Optional<UserResponse> resultOpt = service.authenticateUser(command);

    // Assert
    assertTrue(resultOpt.isEmpty());
  }

  @Test
  @DisplayName("findUserByUsername: Existing user returns UserDto")
  void testFindUserByUsernameFound() {
    // Arrange
    String usernameStr = "user";
    User dummyUser = mock(User.class);

    // Properly mock Long instead of casting
    Long dummyId = 4L;
    when(dummyUser.id()).thenReturn(dummyId.describeConstable());

    when(dummyUser.username()).thenReturn(new Username("user"));
    when(dummyUser.email()).thenReturn(new Email("user@example.com"));
    LocalDateTime fixedDate = LocalDateTime.of(2025, 8, 1, 12, 0, 0, 0);
    when(dummyUser.createdAt()).thenReturn(fixedDate);
    when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(dummyUser));

    // Act
    Optional<UserResponse> resultOpt = service.findUserByUsername(usernameStr);

    // Assert
    assertTrue(resultOpt.isPresent());
    UserResponse dto = resultOpt.get();
    assertEquals(4, dto.id());
    assertEquals("user", dto.username());
    assertEquals("user@example.com", dto.email());
    assertEquals(fixedDate, dto.createdAt());
  }

  @Test
  @DisplayName("findUserByUsername: Non-existing user returns empty")
  void testFindUserByUsernameNotFound() {
    // Arrange
    when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

    // Act
    Optional<UserResponse> resultOpt = service.findUserByUsername("nonexistent");

    // Assert
    assertTrue(resultOpt.isEmpty());
  }

  @Test
  @DisplayName("isUsernameAvailable: Available username returns true")
  void testIsUsernameAvailableTrue() {
    // Arrange
    when(userDomainService.isUsernameAvailable(any(String.class))).thenReturn(true);

    // Act
    boolean available = service.isUsernameAvailable("availableUser");

    // Assert
    assertTrue(available);
  }

  @Test
  @DisplayName("isUsernameAvailable: Unavailable username returns false")
  void testIsUsernameAvailableFalse() {
    // Arrange
    when(userDomainService.isUsernameAvailable(any(String.class))).thenReturn(false);

    // Act
    boolean available = service.isUsernameAvailable("takenUser");

    // Assert
    assertFalse(available);
  }
}