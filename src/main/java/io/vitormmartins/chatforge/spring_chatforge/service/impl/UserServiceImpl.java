package io.vitormmartins.chatforge.spring_chatforge.service.impl;

import io.vitormmartins.chatforge.spring_chatforge.exception.UsernameAlreadyExistsException;
import io.vitormmartins.chatforge.spring_chatforge.model.User;
import io.vitormmartins.chatforge.spring_chatforge.repository.UserRepository;
import io.vitormmartins.chatforge.spring_chatforge.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public boolean isUsernameExists(String username) {
    return userRepository.findByUsername(username).isPresent();
  }

  @Override
  public void createUser(String username, String password, String email) {
    if (password == null) {
      throw new IllegalArgumentException("Password cannot be null");
    }
    String hashedPassword = passwordEncoder.encode(password);
    if (isUsernameExists(username)) {
      throw new UsernameAlreadyExistsException("Username '" + username + "' is already taken");
    }
    userRepository.save(User.builder()
            .username(username)
            .password(hashedPassword)
            .email(email)
            .build());
  }
}