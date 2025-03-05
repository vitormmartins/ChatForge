package io.vitormmartins.chatforge.spring_chatforge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

  private final CustomUserDetailsService customUserDetailsService;

  @Override
  public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
    final String name = authentication.getName();
    final String password = authentication.getCredentials().toString();

    UserDetails userDetails = customUserDetailsService.loadUserByUsername(name);

    if (!userDetails.getUsername().equals(name) || !userDetails.getPassword().equals(password)) {
      return null;
    }

    authentication.setAuthenticated(true);

    return authentication;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
