package io.vitormmartins.chatforge.spring_chatforge.controller;

import io.vitormmartins.chatforge.spring_chatforge.model.User;
import io.vitormmartins.chatforge.spring_chatforge.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/get")
public class MainController {

  private final UserRepository userRepository;

  public MainController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/user/{username}")
  public ResponseEntity<Optional<User>> register(@PathVariable String username) {
    return ResponseEntity.ok(userRepository.findByUsername(username));
  }
}

