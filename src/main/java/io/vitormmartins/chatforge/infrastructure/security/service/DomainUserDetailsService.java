package io.vitormmartins.chatforge.infrastructure.security.service;

import io.vitormmartins.chatforge.domain.user.model.User;
import io.vitormmartins.chatforge.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.springframework.security.core.userdetails.User.withUsername;

/**
 * Spring Security UserDetailsService implementation using the domain repository.
 */
@Service
public class DomainUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return withUsername(user.username().value())
            .password(user.password())
            .authorities("USER") // For now, simple role assignment
            .build();
    }
}
