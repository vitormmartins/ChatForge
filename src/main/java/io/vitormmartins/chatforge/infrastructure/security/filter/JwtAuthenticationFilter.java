package io.vitormmartins.chatforge.infrastructure.security.filter;

import io.vitormmartins.chatforge.infrastructure.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;

/**
 * JWT authentication filter for processing Authorization header tokens.
 */
public class JwtAuthenticationFilter extends AbstractJwtFilter {
    
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        super(jwtUtil);
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
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        boolean hasCookieAuth = hasCookieAuthentication(request);

        if (hasCookieAuth || authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
        if (processToken(token, request, response)) {
            return;
        }
        filterChain.doFilter(request, response);
    }
}
