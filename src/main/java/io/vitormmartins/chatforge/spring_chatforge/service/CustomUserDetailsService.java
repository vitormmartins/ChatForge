package io.vitormmartins.chatforge.spring_chatforge.service;

import io.vitormmartins.chatforge.spring_chatforge.model.User;
import io.vitormmartins.chatforge.spring_chatforge.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.springframework.security.core.userdetails.User.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

 return withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities("USER") // Replace it with actual roles/authorities if available
            .build();
  }
}

