package io.vitormmartins.chatforge.infrastructure.security.adapter;

import io.vitormmartins.chatforge.application.user.service.PasswordEncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adapter that implements the PasswordEncoderPort using Spring Security's PasswordEncoder.
 */
@Component
public class PasswordEncoderAdapter implements PasswordEncoderPort {
    
    private final PasswordEncoder passwordEncoder;
    
    public PasswordEncoderAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
