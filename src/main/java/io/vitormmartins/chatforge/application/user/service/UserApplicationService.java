package io.vitormmartins.chatforge.application.user.service;

import io.vitormmartins.chatforge.application.user.command.AuthenticateUserCommand;
import io.vitormmartins.chatforge.application.user.command.RegisterUserCommand;
import io.vitormmartins.chatforge.application.user.dto.UserResponse;
import io.vitormmartins.chatforge.domain.user.model.User;
import io.vitormmartins.chatforge.domain.user.model.Username;
import io.vitormmartins.chatforge.domain.user.model.Email;
import io.vitormmartins.chatforge.domain.user.repository.UserRepository;
import io.vitormmartins.chatforge.domain.user.service.UserDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Application service for user management use cases.
 * Orchestrates domain objects and coordinates with infrastructure.
 */
@Service
@Transactional
public class UserApplicationService {

  private static final Logger logger = LoggerFactory.getLogger(UserApplicationService.class);

  private final UserRepository userRepository;
  private final UserDomainService userDomainService;
  private final PasswordEncoderPort passwordEncoder;

  public UserApplicationService(
          UserRepository userRepository,
          UserDomainService userDomainService,
          PasswordEncoderPort passwordEncoder) {
    this.userRepository = userRepository;
    this.userDomainService = userDomainService;
    this.passwordEncoder = passwordEncoder;
  }



  /**
   * Registers a new user in the system.
   *
   * @param command the registration command
   * @return the created user response
   * @throws IllegalArgumentException if a username already exists
   */
  public UserResponse registerUser(RegisterUserCommand command) {
    logger.info("Registering new user: {}", command.username());

    // Encode password using infrastructure service
    String encodedPassword = passwordEncoder.encode(command.rawPassword());

    // Create value objects
    Username username = new Username(command.username());
    Email email = command.email() != null ? new Email(command.email()) : null;

    // Use domain service to create a user
    User createdUser = userDomainService.createUser(username, email, encodedPassword);

    logger.info("User registered successfully: {}", createdUser.username());

    // TODO: Publish UserRegisteredEvent

    return UserResponse.from(createdUser);
  }

  /**
   * Authenticates a user with a username and password.
   *
   * @param command the authentication command
   * @return optional user response if authentication succeeds
   */
  @Transactional(readOnly = true)
  public Optional<UserResponse> authenticateUser(AuthenticateUserCommand command) {
    logger.debug("Authenticating user: {}", command.username());

    return userRepository.findByUsername(command.username())
            .filter(user -> {
              boolean matches = passwordEncoder.matches(command.rawPassword(), user.password());
              if (!matches) {
                logger.warn("Authentication failed for user: {}", command.username());
              }
              return matches;
            })
            .map(user -> {
              logger.info("User authenticated successfully: {}", command.username());
              return UserResponse.from(user);
            });
  }

  /**
   * Finds a user by username.
   *
   * @param username the username to search
   * @return optional user response if found
   */
  @Transactional(readOnly = true)
  public Optional<UserResponse> findUserByUsername(String username) {
    logger.debug("Finding user by username: {}", username);
    return userRepository.findByUsername(username)
            .map(UserResponse::from);
  }

  /**
   * Checks if a username is available for registration.
   *
   * @param username the username to check
   * @return true if available, false otherwise
   */
  @Transactional(readOnly = true)
  public boolean isUsernameAvailable(String username) {
    return userDomainService.isUsernameAvailable(username);
  }

  public static void publishUserCreatedEvent(User user) {
  }
}