package io.vitormmartins.chatforge.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.vitormmartins.chatforge.application.user.command.RegisterUserCommand;
import io.vitormmartins.chatforge.application.user.dto.UserResponse;
import io.vitormmartins.chatforge.generated.api.AuthenticationApi;
import io.vitormmartins.chatforge.generated.model.LoginRequest;
import io.vitormmartins.chatforge.generated.model.RegisterRequest;
import io.vitormmartins.chatforge.generated.model.UpdatePasswordRequest;
import io.vitormmartins.chatforge.generated.model.UserDto;
import io.vitormmartins.chatforge.application.user.service.UserApplicationService;
import io.vitormmartins.chatforge.infrastructure.security.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

/**
 * The `AuthController` class provides RESTful endpoints for user authentication and registration.
 * It implements the AuthenticationApi interface and handles login requests, generating JWT tokens
 * upon successful authentication, and processes user registration.
 */
@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Authentication Controller", description = "Auth API endpoints")
public class AuthController implements AuthenticationApi {

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

    @Override
    @PostMapping("/login")
    public ResponseEntity<String> v1AuthLoginPost(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authenticateAndGenerateToken(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(token);
    }

    @Override
    @PostMapping("/login-cookie")
    public ResponseEntity<String> v1AuthLoginCookiePost(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authenticateAndGenerateToken(loginRequest.getUsername(), loginRequest.getPassword());
        ResponseCookie cookie = ResponseCookie.from("auth_token", token)
                                              .httpOnly(true)
                                              .path("/")
                                              .build();
        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, cookie.toString())
                             .body("Authentication successful");
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<UserDto> v1AuthRegisterPost(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterUserCommand command = new RegisterUserCommand(registerRequest.getUsername(),
                                                              registerRequest.getEmail(),
                                                              registerRequest.getPassword()
        );

        UserResponse createdUser = userApplicationService.registerUser(command);
        UserDto userDto = new UserDto();
        userDto.id(createdUser.id());
        userDto.username(createdUser.username());
        userDto.email(createdUser.email());
        userDto.createdAt(createdUser.createdAt()
                                     .atOffset(java.time.ZoneOffset.systemDefault()
                                                                   .getRules()
                                                                   .getOffset(createdUser.createdAt())));

        return ResponseEntity.ok(userDto);
    }

    @Override
    @PutMapping("/update-password")
    public ResponseEntity<Void> v1AuthUpdatePasswordPut(@Valid
                                                        @RequestBody
                                                        UpdatePasswordRequest updatePasswordRequest) {
        // TODO: implement password update logic
        return ResponseEntity.ok().build();
    }

    @Override
    @DeleteMapping("/delete-account")
    public ResponseEntity<Void> v1AuthDeleteAccountDelete() {
        // TODO: implement delete account logic
        return ResponseEntity.noContent().build();
    }

    String authenticateAndGenerateToken(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        return jwtUtil.generateToken(username);
    }
}