package io.vitormmartins.chatforge.spring_chatforge.filter;

import io.vitormmartins.chatforge.spring_chatforge.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class CookieAuthenticationFilter extends OncePerRequestFilter {

  private final JwtDecoder jwtDecoder;
  private final JwtUtil jwtUtil;

  public CookieAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtDecoder = NimbusJwtDecoder.withSecretKey(jwtUtil.getKey()).macAlgorithm(MacAlgorithm.HS512).build();
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response, @NonNull FilterChain filterChain)
          throws ServletException, IOException {
    if (request == null) {
      throw new ServletException("Request cannot be null");
    }
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      try {
        Optional<Cookie> authCookie = Arrays.stream(cookies)
                .filter(cookie -> "auth_token".equals(cookie.getName()))
                .findFirst();

        if (authCookie.isPresent()) {
          String token = authCookie.get().getValue();
          processCookieToken(request, token);
        }
      } catch (JwtException e) {
        if (response != null) {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return;
      }
    }
    filterChain.doFilter(request, response);
  }

  private void processCookieToken(HttpServletRequest request, String token) {
    var claims = jwtUtil.extractAllClaims(token);
    request.setAttribute("claims", claims);
    Jwt jwt = jwtDecoder.decode(token);
    JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);
    jwtAuthenticationToken.setAuthenticated(true);
    SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
  }
}