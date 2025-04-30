package io.vitormmartins.chatforge.spring_chatforge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vitormmartins.chatforge.spring_chatforge.controller.dto.LoginAuthControllerDTO;
import io.vitormmartins.chatforge.spring_chatforge.controller.dto.RegisterAuthControllerDTO;
import io.vitormmartins.chatforge.spring_chatforge.model.User;
import io.vitormmartins.chatforge.spring_chatforge.util.JwtUtil;
import io.vitormmartins.chatforge.spring_chatforge.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "Auth API endpoints")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthController(AuthenticationManager authenticationManager,
                        JwtUtil jwtUtil,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Operation(summary = "Post login", description = "Returns a JWT token")
  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginAuthControllerDTO loginAuthControllerDTO) {
    String token = getToken(loginAuthControllerDTO);
    return ResponseEntity.ok(token);
  }

  @Operation(summary = "Post login-cookie", description = "Returns a JWT token in a cookie")
  @PostMapping("/login-cookie")
  public ResponseEntity<String> loginWithCookie(@RequestBody LoginAuthControllerDTO loginAuthControllerDTO,
                                           HttpServletResponse response) {
    String token = getToken(loginAuthControllerDTO);
    response.addCookie(new Cookie("auth_token", token));
    return ResponseEntity.ok("Cookie set");
  }

  @Operation(summary = "Post register", description = "Creates a new user")
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody RegisterAuthControllerDTO registerAuthControllerDTO) {
    String hashedPassword = passwordEncoder.encode(registerAuthControllerDTO.password());
    userRepository.save(User.builder()
                            .username(registerAuthControllerDTO.username())
                            .password(hashedPassword)
                            .build());
    return ResponseEntity.ok("User registered");
  }

  private String getToken(LoginAuthControllerDTO loginAuthControllerDTO) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginAuthControllerDTO.username(),
                                                                               loginAuthControllerDTO.password()));
    return jwtUtil.generateToken(loginAuthControllerDTO.username());
  }
}

