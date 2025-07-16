package io.vitormmartins.chatforge.config;

import io.vitormmartins.chatforge.infrastructure.security.service.DomainAuthenticationProvider;
import io.vitormmartins.chatforge.infrastructure.security.filter.CookieAuthenticationFilter;
import io.vitormmartins.chatforge.infrastructure.security.filter.JwtAuthenticationFilter;
import io.vitormmartins.chatforge.infrastructure.security.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the application.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final JwtUtil jwtUtil;
    private final DomainAuthenticationProvider authenticationProvider;
    
    public SecurityConfig(JwtUtil jwtUtil, DomainAuthenticationProvider authenticationProvider) {
        this.jwtUtil = jwtUtil;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(cookieAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied"))
            );
        return http.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }
    
    @Bean
    public CookieAuthenticationFilter cookieAuthenticationFilter() {
        return new CookieAuthenticationFilter(jwtUtil);
    }
}
