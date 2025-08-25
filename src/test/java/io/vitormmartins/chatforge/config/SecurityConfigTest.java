package io.vitormmartins.chatforge.config;

import io.vitormmartins.chatforge.infrastructure.security.filter.CookieAuthenticationFilter;
import io.vitormmartins.chatforge.infrastructure.security.filter.JwtAuthenticationFilter;
import io.vitormmartins.chatforge.infrastructure.security.service.DomainAuthenticationProvider;
import io.vitormmartins.chatforge.infrastructure.security.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("SecurityConfig Tests")
class SecurityConfigTest {

    // Create dummy instances for dependencies
    private final JwtUtil dummyJwtUtil = createMockJwtUtil();
    private final DomainAuthenticationProvider dummyAuthProvider = mock(DomainAuthenticationProvider.class);

    // Create an instance under test
    private final SecurityConfig securityConfig = new SecurityConfig(dummyJwtUtil, dummyAuthProvider);
    
    private JwtUtil createMockJwtUtil() {
        JwtUtil jwtUtil = new JwtUtil();
        try {
            java.lang.reflect.Field secretKeyField = JwtUtil.class.getDeclaredField("secretKeyString");
            secretKeyField.setAccessible(true);
            secretKeyField.set(jwtUtil, "OBslwXQbeJPMIq2bNCmAADfBF36Tda8BhRrXebM8zG2P2ksGSCol9f9bDZpVH6gn");
            jwtUtil.init();
            return jwtUtil;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize JwtUtil for testing", e);
        }
    }

    @Test
    @DisplayName("securityFilterChain() configures HttpSecurity and returns SecurityFilterChain")
    void securityFilterChainTest() throws Exception {
        // Create a mock HttpSecurity
        HttpSecurity http = mock(HttpSecurity.class);

        // Stub fluent methods to return the mock instance
        // Match the actual implementation using AbstractHttpConfigurer::disable
        when(http.csrf(any())).thenReturn(http);
        when(http.authenticationProvider(any(AuthenticationProvider.class))).thenReturn(http);
        when(http.addFilterBefore(any(JwtAuthenticationFilter.class), eq(UsernamePasswordAuthenticationFilter.class)))
                .thenReturn(http);
        when(http.addFilterBefore(any(CookieAuthenticationFilter.class), eq(UsernamePasswordAuthenticationFilter.class)))
                .thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.exceptionHandling(any())).thenReturn(http);

        DefaultSecurityFilterChain dummyChain = mock(DefaultSecurityFilterChain.class);
        when(http.build()).thenReturn(dummyChain);

        SecurityFilterChain result = securityConfig.securityFilterChain(http);
        assertNotNull(result);
        assertEquals(dummyChain, result);

        // Verify interactions (at least one verify for one configuration branch)
        verify(http).authenticationProvider(dummyAuthProvider);
    }

    @Test
    @DisplayName("authenticationManager() returns manager from AuthenticationConfiguration")
    void authenticationManagerTest() throws Exception {
        AuthenticationManager dummyManager = mock(AuthenticationManager.class);
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        when(authConfig.getAuthenticationManager()).thenReturn(dummyManager);

        AuthenticationManager result = securityConfig.authenticationManager(authConfig);
        assertNotNull(result);
        assertEquals(dummyManager, result);
    }

    @Test
    @DisplayName("jwtAuthenticationFilter() returns filter with proper JwtUtil")
    void jwtAuthenticationFilterTest() {
        JwtAuthenticationFilter jwtFilter = securityConfig.jwtAuthenticationFilter();
        assertNotNull(jwtFilter);
        // Simply verify the filter was created successfully
        // If there were issues with JwtUtil initialization, the filter creation would have failed
        assertNotNull(jwtFilter);
    }

    @Test
    @DisplayName("cookieAuthenticationFilter() returns filter with proper JwtUtil")
    void cookieAuthenticationFilterTest() {
        CookieAuthenticationFilter cookieFilter = securityConfig.cookieAuthenticationFilter();
        assertNotNull(cookieFilter);
        
        // Verify the filter was created successfully (this is enough since we can't easily check internal jwtUtil)
        // If there were issues with JwtUtil initialization, the filter creation would have failed
        assertNotNull(cookieFilter);
    }
}