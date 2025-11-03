package io.vitormmartins.chatforge.config;

import io.vitormmartins.chatforge.application.user.service.PasswordEncoderPort;
import io.vitormmartins.chatforge.application.user.service.UserApplicationService;
import io.vitormmartins.chatforge.domain.user.repository.UserRepository;
import io.vitormmartins.chatforge.domain.user.service.UserDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Application-wide configuration beans.
 */
@Configuration
public class ApplicationConfig {

  @Bean
  public UserDomainService userDomainService(UserRepository userRepository) {
    return new UserDomainService(userRepository);
  }

  @Bean
  public UserApplicationService userApplicationService(
          UserRepository userRepository,
          UserDomainService userDomainService,
          PasswordEncoderPort passwordEncoder) {
    return new UserApplicationService(userRepository, userDomainService, passwordEncoder);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}


