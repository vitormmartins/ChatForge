package io.vitormmartins.chatforge.spring_chatforge.controller;

import io.vitormmartins.chatforge.spring_chatforge.model.User;
import io.vitormmartins.chatforge.spring_chatforge.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller for user operations.
 */
@RestController
@RequestMapping("/get")
public class MainController {

  private final UserRepository userRepository;

  /**
   * Constructor for MainController.
   *
   * @param userRepository the repository for User operations
   */
  public MainController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Retrieves a user by username.
   *
   * @param username the username to search for
   * @return ResponseEntity containing an Optional with the user if found
   */
  @GetMapping("/user/{username}")
  public ResponseEntity<Optional<User>> register(@PathVariable String username) {
    return ResponseEntity.ok(userRepository.findByUsername(username));
  }
}
