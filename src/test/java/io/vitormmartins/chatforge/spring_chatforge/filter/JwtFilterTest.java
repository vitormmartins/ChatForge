// File: src/test/java/io/vitormmartins/chatforge/spring_chatforge/filter/JwtFilterTest.java
package io.vitormmartins.chatforge.spring_chatforge.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.vitormmartins.chatforge.spring_chatforge.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

    private static final String SECRET_KEY = "OBslwXQbeJPMIq2bNCmAADfBF36Tda8BhRrXebM8zG2P2ksGSCol9f9bDZpVH6gn";
    private static final String TEST_USERNAME = "testuser";
    public static final int EXPIRATION_INTERVAL_MILLIS = 1000 * 60 * 60 * 2;

    private JwtUtil jwtUtil;
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKeyString", SECRET_KEY);
        jwtUtil.init();
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil);
    }

    // JwtUtil tests

    @Test
    @DisplayName("generateToken returns a valid token")
    void generateToken_shouldReturnValidToken() {
        String token = jwtUtil.generateToken(TEST_USERNAME);
        assertNotNull(token);
    }

    @Test
    @DisplayName("extractAllClaims returns claims for a valid token")
    void extractAllClaims_shouldReturnClaims_whenTokenIsValid() {
        String token = jwtUtil.generateToken(TEST_USERNAME);
        Jws<Claims> claims = jwtUtil.extractAllClaims(token);
        assertEquals(TEST_USERNAME, claims.getPayload().getSubject());
    }

    @Test
    @DisplayName("extractAllClaims throws exception when token is expired")
    void extractAllClaims_shouldThrowException_whenTokenIsExpired() {
        String expiredToken = Jwts.builder()
                .subject(TEST_USERNAME)
                .issuedAt(new Date(System.currentTimeMillis() - EXPIRATION_INTERVAL_MILLIS))
                .expiration(new Date(System.currentTimeMillis() - EXPIRATION_INTERVAL_MILLIS))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .compact();
        assertThrows(RuntimeException.class, () -> jwtUtil.extractAllClaims(expiredToken));
    }

    @Test
    @DisplayName("extractAllClaims throws exception when token is invalid")
    void extractAllClaims_shouldThrowException_whenTokenIsInvalid() {
        String invalidToken = "invalid.token.here";
        assertThrows(RuntimeException.class, () -> jwtUtil.extractAllClaims(invalidToken));
    }

    @Test
    @DisplayName("init throws exception when secret key is too short")
    void init_shouldThrowException_whenSecretKeyIsTooShort() {
        JwtUtil shortKeyJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(shortKeyJwtUtil, "secretKeyString", "short_key");
        assertThrows(IllegalArgumentException.class, shortKeyJwtUtil::init);
    }

    // Filter tests

    @Test
    @DisplayName("Valid token should set authentication and claims attribute")
    void doFilterInternal_validToken_setsAuthenticationAndAttributes() throws ServletException, IOException {
        String token = jwtUtil.generateToken(TEST_USERNAME);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(TEST_USERNAME, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(request).setAttribute(eq("claims"), any(Jws.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Missing authorization header should continue filter chain")
    void doFilterInternal_missingAuthorization_shouldContinueChain() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Invalid token format should continue filter chain")
    void doFilterInternal_invalidTokenFormat_shouldContinueChain() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat token");
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Invalid token should not set authentication and return unauthorized")
    void doFilterInternal_invalidToken_returnsUnauthorized() throws ServletException, IOException {
        String invalidToken = "invalid.token.here";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Expired token should not set authentication and return unauthorized")
    void doFilterInternal_expiredToken_returnsUnauthorized() throws ServletException, IOException {
        String expiredToken = Jwts.builder()
                .subject(TEST_USERNAME)
                .issuedAt(Date.from(Instant.now().minusSeconds(7200)))
                .expiration(Date.from(Instant.now().minusSeconds(3600)))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .compact();
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Token with invalid signature should return unauthorized")
    void doFilterInternal_invalidSignature_returnsUnauthorized() throws ServletException, IOException {
        String tokenWithInvalidSignature = Jwts.builder()
                .subject(TEST_USERNAME)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(Keys.hmacShaKeyFor("different_secret_key_that_is_also_32_chars".getBytes(StandardCharsets.UTF_8)))
                .compact();
        when(request.getHeader("Authorization")).thenReturn("Bearer " + tokenWithInvalidSignature);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Malformed token should return unauthorized")
    void doFilterInternal_malformedToken_returnsUnauthorized() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer malformed.jwt.token");
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }
}