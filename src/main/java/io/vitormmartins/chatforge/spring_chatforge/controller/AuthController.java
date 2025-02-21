package io.vitormmartins.chatforge.spring_chatforge.controller;

import io.vitormmartins.chatforge.spring_chatforge.util.JwtUtil;
import io.vitormmartins.chatforge.spring_chatforge.model.User;
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
  public ResponseEntity<?> login(@RequestBody User user) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
    String token = jwtUtil.generateToken(user.getUsername());
    return ResponseEntity.ok(token);
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody User user) {
    userRepository.save(user);
    return ResponseEntity.ok("User registered");
  }
}

