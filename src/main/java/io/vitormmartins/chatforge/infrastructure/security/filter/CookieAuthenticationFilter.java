package io.vitormmartins.chatforge.infrastructure.security.filter;

import io.vitormmartins.chatforge.infrastructure.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;

/**
 * Authentication filter for processing JWT tokens from cookies.
 */
public class CookieAuthenticationFilter extends AbstractJwtFilter {

    public CookieAuthenticationFilter(JwtUtil jwtUtil) {
        super(jwtUtil);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        var tokenOptional = extractTokenFromCookies(request);
        if (tokenOptional.isPresent()) {
            String token = tokenOptional.get();
            if (processToken(token, request, response)) {
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
