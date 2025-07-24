package io.vitormmartins.chatforge.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vitormmartins.chatforge.application.user.dto.RegisterUserCommand;
import io.vitormmartins.chatforge.application.user.dto.UserDto;
import io.vitormmartins.chatforge.application.user.service.UserApplicationService;
import io.vitormmartins.chatforge.web.controller.dto.LoginRequest;
import io.vitormmartins.chatforge.web.controller.dto.RegisterRequest;
import io.vitormmartins.chatforge.infrastructure.security.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

/**
 * The `AuthController` class provides RESTful endpoints for user authentication and registration.
 * It handles login requests, generating JWT tokens upon successful authentication, and processes
 * user registration. This controller integrates with Spring Security for authentication and delegates
 * user creation to the {@link UserApplicationService}.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "Auth API endpoints")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserApplicationService userApplicationService;
    
    /**
     * Constructs a new `AuthController` with the necessary services for authentication and user management.
     *
     * @param authenticationManager the Spring Security authentication manager
     * @param jwtUtil the utility for generating JWT tokens
     * @param userApplicationService the application service for user-related operations
     */
    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserApplicationService userApplicationService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userApplicationService = userApplicationService;
    }
    
    /**
     * Handles user login requests. Authenticates the user and returns a JWT token in the response body.
     *
     * @param request the login request containing the username and password
     * @return a `ResponseEntity` with the JWT token if authentication is successful
     */
    @Operation(summary = "User login", description = "Authenticates user and returns JWT token")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = authenticateAndGenerateToken(request.username(), request.password());
        return ResponseEntity.ok(token);
    }
    
    /**
     * Handles user login requests and sets the JWT token in an HTTP cookie.
     *
     * @param request the login request containing the username and password
     * @param response the `HttpServletResponse` to which the cookie will be added
     * @return a `ResponseEntity` with a success message
     */
    @Operation(summary = "User login with cookie", description = "Authenticates user and sets JWT in cookie")
    @PostMapping("/login-cookie")
    public ResponseEntity<String> loginWithCookie(
            @RequestBody LoginRequest request,
            HttpServletResponse response) {
        String token = authenticateAndGenerateToken(request.username(), request.password());
        response.addCookie(new Cookie("auth_token", token));
        return ResponseEntity.ok("Authentication successful");
    }
    
    /**
     * Handles user registration requests. Creates a new user account based on the provided details.
     *
     * @param request the registration request containing username, email, and password
     * @return a `ResponseEntity` with the created user's data
     */
    @Operation(summary = "User registration", description = "Creates a new user account")
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
            request.username(),
            request.email(),
            request.password()
        );
        
        UserDto createdUser = userApplicationService.registerUser(command);
        return ResponseEntity.ok(createdUser);
    }
    
    /**
     * Authenticates the user with the provided credentials and generates a JWT token upon success.
     *
     * @param username the username to authenticate
     * @param password the user's password
     * @return a JWT token as a string
     */
    private String authenticateAndGenerateToken(String username, String password) {
        // Authenticate using Spring Security
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
        
        // Generate JWT token
        return jwtUtil.generateToken(username);
    }
}
