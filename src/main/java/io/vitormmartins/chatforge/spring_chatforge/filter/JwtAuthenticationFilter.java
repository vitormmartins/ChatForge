package io.vitormmartins.chatforge.spring_chatforge.filter;

import io.vitormmartins.chatforge.spring_chatforge.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtDecoder jwtDecoder;
  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtDecoder = NimbusJwtDecoder.withSecretKey(jwtUtil.getKey()).macAlgorithm(MacAlgorithm.HS512).build();
    this.jwtUtil = jwtUtil;
  }

/**
 * Processes incoming requests to validate JWT tokens and set authentication in the security context.
 *
 * @param request     the HTTP request containing the JWT token in the Authorization header
 * @param response    the HTTP response to be sent back to the client
 * @param filterChain the chain of filters to pass the request and response to the next filter
 * @throws IOException      if an input or output error occurs while processing the request
 * @throws ServletException if the request could not be handled
 */
  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain)
          throws IOException, ServletException {

    // Validate JWT for all other requests
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring(7);
    try {
      var claims = jwtUtil.extractAllClaims(token);
      request.setAttribute("claims", claims);

      Jwt jwt = jwtDecoder.decode(token);
      JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);
      jwtAuthenticationToken.setAuthenticated(true);
      SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
    } catch (JwtException e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    filterChain.doFilter(request, response);
  }
}

