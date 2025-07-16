package io.vitormmartins.chatforge.infrastructure.security.service;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Custom authentication provider using the domain-based user details service.
 */
@Component
public class DomainAuthenticationProvider implements AuthenticationProvider {
    
    private final DomainUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    
    public DomainAuthenticationProvider(DomainUserDetailsService userDetailsService,
                                        PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();
        
        // Load user details from the domain
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        // Validate credentials
        if (!userDetails.getUsername().equals(username) || 
            !passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        
        // Create authenticated token
        return new UsernamePasswordAuthenticationToken(
            userDetails,
            password,
            userDetails.getAuthorities()
        );
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
