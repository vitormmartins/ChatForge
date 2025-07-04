package io.vitormmartins.chatforge.infrastructure.security.filter;

import io.vitormmartins.chatforge.infrastructure.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

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

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> authCookie = Arrays.stream(cookies)
                    .filter(cookie -> "auth_token".equals(cookie.getName()))
                    .findFirst();
            if (authCookie.isPresent()) {
                String token = authCookie.get().getValue();
                if (processToken(token, request, response)) {
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
