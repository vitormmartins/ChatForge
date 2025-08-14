package io.vitormmartins.chatforge.web.controller;

import io.vitormmartins.chatforge.application.user.service.UserApplicationService;
import io.vitormmartins.chatforge.generated.model.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * The `UserController` class provides RESTful endpoints for managing user-related operations
 * such as retrieving user information and checking for username availability. It delegates the
 * business logic to the {@link UserApplicationService}.
 */
@RestController
@RequestMapping("/v1/api/users")
public class UserController {
    
    private final UserApplicationService userApplicationService;
    
    /**
     * Constructs a new `UserController` with the specified `UserApplicationService`.
     *
     * @param userApplicationService the application service for user-related operations
     */
    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }
    
    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return a `ResponseEntity` containing the `UserDto` if found, or a 404 Not Found response
     */
    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        Optional<UserDto> user = userApplicationService.findUserByUsername(username);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Checks if a username is available.
     *
     * @param username the username to check
     * @return a `ResponseEntity` containing `true` if the username is available, and `false` otherwise
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsernameAvailability(@PathVariable String username) {
        boolean available = userApplicationService.isUsernameAvailable(username);
        return ResponseEntity.ok(available);
    }
}
