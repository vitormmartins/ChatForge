package io.vitormmartins.chatforge.spring_chatforge.config;

import io.vitormmartins.chatforge.spring_chatforge.filter.CookieAuthenticationFilter;
import io.vitormmartins.chatforge.spring_chatforge.filter.JwtAuthenticationFilter;
import io.vitormmartins.chatforge.spring_chatforge.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private final JwtUtil jwtUtil;

  public SecurityConfig(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for stateless APIs
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)  // Add JWT filter
            .addFilterBefore(cookieAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)  // Add cookie filter
            .authorizeHttpRequests(authz -> authz
                    .requestMatchers("/auth/**").permitAll()  // Allow access to /auth endpoints
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/v3/api-docs/**").permitAll()
                    .requestMatchers("/swagger-ui.html").permitAll()
                    .anyRequest().authenticated()            // Require authentication for all other requests
            )
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint((request,
                                               response,
                                               authException) ->
                            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"))
                    .accessDeniedHandler((request,
                                          response,
                                          accessDeniedException) ->
                            response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied"))
            );
    return http.build();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter(jwtUtil);
  }

  @Bean
  public CookieAuthenticationFilter cookieAuthenticationFilter() {
    return new CookieAuthenticationFilter(jwtUtil);
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }
}

