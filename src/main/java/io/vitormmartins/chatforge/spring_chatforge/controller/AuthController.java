package io.vitormmartins.chatforge.spring_chatforge.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;

  public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.userRepository = userRepository;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginAuthControllerDTO loginAuthControllerDTO) {
    String token = getToken(loginAuthControllerDTO);
    return ResponseEntity.ok(token);
  }

  @PostMapping("/login-cookie")
  public ResponseEntity<?> loginWithCookie(@RequestBody LoginAuthControllerDTO loginAuthControllerDTO,
                                           HttpServletResponse response) {
    String token = getToken(loginAuthControllerDTO);
    response.addCookie(new Cookie("auth_token", token));
    return ResponseEntity.ok("Cookie set");
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterAuthControllerDTO registerAuthControllerDTO) {
    userRepository.save(User.builder()
            .username(registerAuthControllerDTO.username())
            .password(registerAuthControllerDTO.password())
            .build());
    return ResponseEntity.ok("User registered");
  }

  private String getToken(LoginAuthControllerDTO loginAuthControllerDTO) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginAuthControllerDTO.username(),
            loginAuthControllerDTO.password()));
    return jwtUtil.generateToken(loginAuthControllerDTO.username());
  }
}

