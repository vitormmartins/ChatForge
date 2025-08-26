package io.vitormmartins.chatforge.config;

import io.vitormmartins.chatforge.infrastructure.security.filter.CookieAuthenticationFilter;
import io.vitormmartins.chatforge.infrastructure.security.filter.JwtAuthenticationFilter;
import io.vitormmartins.chatforge.infrastructure.security.service.DomainAuthenticationProvider;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Test configuration for the authentication filters.
 * Provides mock filters to avoid real token processing in tests.
 */
@TestConfiguration
public class TestAuthenticationConfig {

    @Bean
    @Primary
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return Mockito.mock(JwtAuthenticationFilter.class);
    }
    
    @Bean
    @Primary
    public CookieAuthenticationFilter cookieAuthenticationFilter() {
        return Mockito.mock(CookieAuthenticationFilter.class);
    }
    
    @Bean
    @Primary
    public DomainAuthenticationProvider domainAuthenticationProvider() {
        return Mockito.mock(DomainAuthenticationProvider.class);
    }
}
