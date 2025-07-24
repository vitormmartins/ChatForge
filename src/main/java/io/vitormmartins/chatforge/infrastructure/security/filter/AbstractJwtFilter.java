package io.vitormmartins.chatforge.infrastructure.security.filter;

import io.vitormmartins.chatforge.infrastructure.security.exception.InvalidTokenException;
import io.vitormmartins.chatforge.infrastructure.security.exception.TokenExpiredException;
import io.vitormmartins.chatforge.infrastructure.security.util.JwtUtil;
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
}
