package io.vitormmartins.chatforge.application.user.service;

import io.jsonwebtoken.security.Password; // TODO: insert this class
import io.vitormmartins.chatforge.application.user.dto.AuthenticateUserCommand;
import io.vitormmartins.chatforge.application.user.dto.RegisterUserCommand;
import io.vitormmartins.chatforge.domain.user.model.*;
import io.vitormmartins.chatforge.domain.user.repository.UserRepository;
import io.vitormmartins.chatforge.domain.user.service.UserDomainService;
import io.vitormmartins.chatforge.generated.model.UserDto;
import jakarta.validation.constraints.Email; // TODO: insert this class
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
    when(dummyUser.getId()).thenReturn(dummyId.describeConstable());

    when(dummyUser.getUsername()).thenReturn("user");
    when(dummyUser.getEmail()).thenReturn("user@example.com");
    OffsetDateTime fixedDate = OffsetDateTime.of( 2025,
                                                  7,
                                                  23,
                                                  21,
                                                  28,
                                                  55,
                                                  0,
                                                  ZoneOffset.UTC);
    when(dummyUser.getCreatedAt()).thenReturn(fixedDate.toLocalDateTime());
    when(userDomainService.createUser(
            ArgumentMatchers.any(String.class),
            ArgumentMatchers.any(),
            ArgumentMatchers.any(String.class))
    ).thenReturn(dummyUser);

    // Act
    UserDto dto = service.registerUser(command);

    // Assert
    assertNotNull(dto);
    assertEquals(1, dto.getId());
    assertEquals("user", dto.getUsername());
    assertEquals("user@example.com", dto.getEmail());
    assertEquals(fixedDate, dto.getCreatedAt());
  }

  @Test
  @DisplayName("authenticateUser: Valid credentials returns UserDto")
  void testAuthenticateUserSuccess() {
    // Arrange
    AuthenticateUserCommand command = new AuthenticateUserCommand("user", "pass");
    User dummyUser = mock(User.class);

    // Properly mock Long instead of casting
    Long dummyId = 2L;
    when(dummyUser.getId()).thenReturn(dummyId.describeConstable());

    when(dummyUser.getUsername()).thenReturn("user");
    when(dummyUser.getEmail()).thenReturn("user@example.com");
    OffsetDateTime fixedDate = OffsetDateTime.of( 2025,
                                                  7,
                                                  24,
                                                  10,
                                                  0,
                                                  0,
                                                  0,
                                                  ZoneOffset.UTC);
    when(dummyUser.getCreatedAt()).thenReturn(fixedDate.toLocalDateTime());
    // Stub password checking
    // Simulates that a user's password encoded value is "encodedPass"
    // To simulate, we assume dummyUser.getPassword() returns dummyPassword.
    when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(dummyUser));
    when(passwordEncoder.matches(eq("pass"), any())).thenReturn(true);

    // Act
    Optional<UserDto> resultOpt = service.authenticateUser(command);

    // Assert
    assertTrue(resultOpt.isPresent());
    UserDto dto = resultOpt.get();
    assertEquals(2, dto.getId());
    assertEquals("user", dto.getUsername());
    assertEquals("user@example.com", dto.getEmail());
    assertEquals(fixedDate, dto.getCreatedAt());
  }

  @Test
  @DisplayName("authenticateUser: Wrong password returns empty")
  void testAuthenticateUserFailure() {
    // Arrange
    AuthenticateUserCommand command = new AuthenticateUserCommand("user", "wrongpass");
    User dummyUser = mock(User.class);

    // Properly mock Long instead of casting
    Long dummyId = 3L;
    when(dummyUser.getId()).thenReturn(dummyId.describeConstable());

    when(dummyUser.getUsername()).thenReturn("user");
    when(dummyUser.getEmail()).thenReturn("user@example.com");
    OffsetDateTime fixedDate = OffsetDateTime.now(ZoneOffset.UTC);
    when(dummyUser.getCreatedAt()).thenReturn(fixedDate.toLocalDateTime());
    when(dummyUser.getPassword()).thenReturn("pass");
    when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(dummyUser));

    // Act
    Optional<UserDto> resultOpt = service.authenticateUser(command);

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
    when(dummyUser.getId()).thenReturn(dummyId.describeConstable());

    when(dummyUser.getUsername()).thenReturn("user");
    when(dummyUser.getEmail()).thenReturn("user@example.com");
    OffsetDateTime fixedDate = OffsetDateTime.of(2025, 8, 1, 12, 0, 0, 0, ZoneOffset.UTC);
    when(dummyUser.getCreatedAt()).thenReturn(fixedDate.toLocalDateTime());
    when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(dummyUser));

    // Act
    Optional<UserDto> resultOpt = service.findUserByUsername(usernameStr);

    // Assert
    assertTrue(resultOpt.isPresent());
    UserDto dto = resultOpt.get();
    assertEquals(4, dto.getId());
    assertEquals("user", dto.getUsername());
    assertEquals("user@example.com", dto.getEmail());
    assertEquals(fixedDate, dto.getCreatedAt());
  }

  @Test
  @DisplayName("findUserByUsername: Non-existing user returns empty")
  void testFindUserByUsernameNotFound() {
    // Arrange
    when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

    // Act
    Optional<UserDto> resultOpt = service.findUserByUsername("nonexistent");

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