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

/**
 * Security configuration for the application.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private final JwtUtil jwtUtil;

  /**
   * Constructor for SecurityConfig.
   *
   * @param jwtUtil the JWT utility
   */
  public SecurityConfig(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  /**
   * Creates a BCryptPasswordEncoder bean.
   *
   * @return a PasswordEncoder instance
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Configures the security filter chain.
   *
   * @param http the HttpSecurity instance
   * @return configured SecurityFilterChain
   * @throws Exception if an error occurs during configuration
   */
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

  /**
   * Creates the JwtAuthenticationFilter bean.
   *
   * @return a JwtAuthenticationFilter instance
   */
  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter(jwtUtil);
  }

  /**
   * Creates the CookieAuthenticationFilter bean.
   *
   * @return a CookieAuthenticationFilter instance
   */
  @Bean
  public CookieAuthenticationFilter cookieAuthenticationFilter() {
    return new CookieAuthenticationFilter(jwtUtil);
  }

  /**
   * Provides the AuthenticationManager bean.
   *
   * @param authConfig the authentication configuration
   * @return an AuthenticationManager instance
   * @throws Exception if an error occurs while getting the authentication manager
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }
}
