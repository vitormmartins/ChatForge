package io.vitormmartins.chatforge.application.user.service;

import io.vitormmartins.chatforge.application.user.dto.AuthenticateUserCommand;
import io.vitormmartins.chatforge.application.user.dto.RegisterUserCommand;
import io.vitormmartins.chatforge.application.user.dto.UserDto;
import io.vitormmartins.chatforge.domain.user.model.*;
import io.vitormmartins.chatforge.domain.user.repository.UserRepository;
import io.vitormmartins.chatforge.domain.user.service.UserDomainService;

import java.util.Optional;

/**
 * Application service for user management use cases.
 * Orchestrates domain objects and coordinates with infrastructure.
 */
public class UserApplicationService {
    
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
     */
    public UserDto registerUser(RegisterUserCommand command) {
        // Encode password using infrastructure service
        String encodedPassword = passwordEncoder.encode(command.rawPassword());
        
        // Create domain value objects
        Username username = new Username(command.username());
        Email email = command.email() != null ? new Email(command.email()) : null;
        Password password = Password.fromEncoded(encodedPassword);
        
        // Use domain service to create user
        User createdUser = userDomainService.createUser(username, email, password);
        
        // Convert to DTO
        return mapToDto(createdUser);
    }
    
    /**
     * Authenticates a user with username and password.
     */
    public Optional<UserDto> authenticateUser(AuthenticateUserCommand command) {
        Username username = new Username(command.username());
        
        return userRepository.findByUsername(username)
            .filter(user -> passwordEncoder.matches(command.rawPassword(), user.getPassword().getEncodedValue()))
            .map(this::mapToDto);
    }
    
    /**
     * Finds a user by username.
     */
    public Optional<UserDto> findUserByUsername(String username) {
        Username usernameObj = new Username(username);
        return userRepository.findByUsername(usernameObj)
            .map(this::mapToDto);
    }
    
    /**
     * Checks if a username is available for registration.
     */
    public boolean isUsernameAvailable(String username) {
        Username usernameObj = new Username(username);
        return userDomainService.isUsernameAvailable(usernameObj);
    }
    
    private UserDto mapToDto(User user) {
        return new UserDto(
            user.getId() != null ? user.getId().getValue() : null,
            user.getUsername().getValue(),
            user.getEmail() != null ? user.getEmail().getValue() : null,
            user.getCreatedAt()
        );
    }
}
