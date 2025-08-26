package io.vitormmartins.chatforge.config;

import io.vitormmartins.chatforge.application.user.service.PasswordEncoderPort;
import io.vitormmartins.chatforge.application.user.service.UserApplicationService;
import io.vitormmartins.chatforge.domain.user.repository.UserRepository;
import io.vitormmartins.chatforge.domain.user.service.UserDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for application layer beans.
 * Wires domain services and application services.
 */
@Configuration
/**
 * Application-wide configuration beans.
 */
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
}
