package io.vitormmartins.chatforge.spring_chatforge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vitormmartins.chatforge.spring_chatforge.controller.dto.LoginAuthControllerDTO;
import io.vitormmartins.chatforge.spring_chatforge.controller.dto.RegisterAuthControllerDTO;
import io.vitormmartins.chatforge.spring_chatforge.service.UserService;
import io.vitormmartins.chatforge.spring_chatforge.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for authentication endpoints.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "Auth API endpoints")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserService userService;

  /**
   * Constructor for AuthController.
   *
   * @param authenticationManager the authentication manager
   * @param jwtUtil the JWT utility
   */
  public AuthController(AuthenticationManager authenticationManager,
                        JwtUtil jwtUtil,
                        UserService userService) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.userService = userService;
  }

  /**
   * Authenticates a user and returns a JWT token.
   *
   * @param loginAuthControllerDTO the login details
   * @return ResponseEntity containing the JWT token
   */
  @Operation(summary = "Post login", description = "Returns a JWT token")
  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginAuthControllerDTO loginAuthControllerDTO) {
    String token = getToken(loginAuthControllerDTO);
    return ResponseEntity.ok(token);
  }

  /**
   * Authenticates a user, sets the JWT token in a cookie, and returns a confirmation.
   *
   * @param loginAuthControllerDTO the login details
   * @param response the HTTP servlet response
   * @return ResponseEntity with a cookie set confirmation
   */
  @Operation(summary = "Post login-cookie", description = "Returns a JWT token in a cookie")
  @PostMapping("/login-cookie")
  public ResponseEntity<String> loginWithCookie(@RequestBody LoginAuthControllerDTO loginAuthControllerDTO,
                                           HttpServletResponse response) {
    String token = getToken(loginAuthControllerDTO);
    response.addCookie(new Cookie("auth_token", token));
    return ResponseEntity.ok("Cookie set");
  }

  /**
   * Registers a new user.
   *
   * @param registerAuthControllerDTO the registration details
   * @return ResponseEntity confirming user registration
   */
  @Operation(summary = "Post register", description = "Creates a new user")
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody RegisterAuthControllerDTO registerAuthControllerDTO) {
    userService.createUser(registerAuthControllerDTO.username(),
                           registerAuthControllerDTO.password(),
                           registerAuthControllerDTO.email());
    return ResponseEntity.ok("User registered");
  }

  /**
   * Authenticates a user and generates a JWT token.
   *
   * @param loginAuthControllerDTO the login details
   * @return the generated JWT token
   */
  private String getToken(LoginAuthControllerDTO loginAuthControllerDTO) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginAuthControllerDTO.username(),
                                                                               loginAuthControllerDTO.password()));
    return jwtUtil.generateToken(loginAuthControllerDTO.username());
  }
}
