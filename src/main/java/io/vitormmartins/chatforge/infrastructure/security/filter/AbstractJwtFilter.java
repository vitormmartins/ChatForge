package io.vitormmartins.chatforge.infrastructure.security.filter;

import io.vitormmartins.chatforge.infrastructure.security.exception.InvalidTokenException;
import io.vitormmartins.chatforge.infrastructure.security.exception.TokenExpiredException;
import io.vitormmartins.chatforge.infrastructure.security.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;
import java.util.Optional;

/**
 * Abstract base class for JWT authentication filters.
 * Moved to the infrastructure layer as it's a technical concern.
 */
public abstract class AbstractJwtFilter extends OncePerRequestFilter {
  protected final JwtUtil jwtUtil;
  protected final JwtDecoder jwtDecoder;

  protected AbstractJwtFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
    this.jwtDecoder = NimbusJwtDecoder.withSecretKey(jwtUtil.getKey())
            .macAlgorithm(MacAlgorithm.HS512)
            .build();
  }

  /**
   * Processes provided JWT token for authentication.
   * <p>
   * Extracts claims, sets them as a request attribute, decodes the token,
   * and sets the authentication in the security context. If the token is invalid
   * or expired, it sets the response status to unauthorized.
   *
   * @param token    the JWT token to process
   * @param request  the HTTP servlet request
   * @param response the HTTP servlet response
   * @return {@code false} if authentication succeeds, {@code true} if unauthorized
   */
  protected boolean processToken(String token, HttpServletRequest request, HttpServletResponse response) {
    try {
      var claims = jwtUtil.extractAllClaims(token);
      request.setAttribute("claims", claims);
      Jwt jwt = jwtDecoder.decode(token);
      JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt);
      authToken.setAuthenticated(true);
      SecurityContextHolder.getContext().setAuthentication(authToken);
      return false;
    } catch (InvalidTokenException | TokenExpiredException | JwtException e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return true;
    }
  }

  /**
   * Extracts the authentication token from cookies if present.
   *
   * @param request The HTTP request containing cookies
   * @return An Optional containing the auth token if found, empty otherwise
   */
  protected Optional<String> extractTokenFromCookies(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return Optional.empty();
    }

    return Arrays.stream(cookies)
            .filter(cookie -> "auth_token".equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst();
  }
}
