package io.vitormmartins.chatforge.spring_chatforge.controller;

import io.vitormmartins.chatforge.spring_chatforge.controller.dto.LoginAuthControllerDTO;
import io.vitormmartins.chatforge.spring_chatforge.controller.dto.RegisterAuthControllerDTO;
import io.vitormmartins.chatforge.spring_chatforge.model.User;
import io.vitormmartins.chatforge.spring_chatforge.util.JwtUtil;
import io.vitormmartins.chatforge.spring_chatforge.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

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
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginAuthControllerDTO.username(),
                                                                               loginAuthControllerDTO.password()));
    String token = jwtUtil.generateToken(loginAuthControllerDTO.username());
    return ResponseEntity.ok(token);
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterAuthControllerDTO registerAuthControllerDTO) {
    userRepository.save(User.builder()
                            .username(registerAuthControllerDTO.username())
                            .password(registerAuthControllerDTO.password())
                            .build());
    return ResponseEntity.ok("User registered");
  }
}

