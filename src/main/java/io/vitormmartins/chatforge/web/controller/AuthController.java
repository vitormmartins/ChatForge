package io.vitormmartins.chatforge.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vitormmartins.chatforge.application.user.dto.AuthenticateUserCommand;
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

import java.util.Optional;

/**
 * Authentication controller using the new hexagonal architecture.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "Auth API endpoints")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserApplicationService userApplicationService;
    
    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserApplicationService userApplicationService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userApplicationService = userApplicationService;
    }
    
    @Operation(summary = "User login", description = "Authenticates user and returns JWT token")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = authenticateAndGenerateToken(request.username(), request.password());
        return ResponseEntity.ok(token);
    }
    
    @Operation(summary = "User login with cookie", description = "Authenticates user and sets JWT in cookie")
    @PostMapping("/login-cookie")
    public ResponseEntity<String> loginWithCookie(
            @RequestBody LoginRequest request,
            HttpServletResponse response) {
        String token = authenticateAndGenerateToken(request.username(), request.password());
        response.addCookie(new Cookie("auth_token", token));
        return ResponseEntity.ok("Authentication successful");
    }
    
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
    
    private String authenticateAndGenerateToken(String username, String password) {
        // Authenticate using Spring Security
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
        
        // Generate JWT token
        return jwtUtil.generateToken(username);
    }
}
