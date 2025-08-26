package io.vitormmartins.chatforge.infrastructure.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for JwtUtil.
 */
class JwtUtilTest {

    private static final String SECRET_KEY = "ThisIsATestSecretKeyForTestingThatMustBeAtLeast32Chars";
    private static final String TEST_USERNAME = "testuser";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKeyString", SECRET_KEY);
        jwtUtil.init();
    }

    @Test
    @DisplayName("generateToken returns a valid token")
    void generateToken_shouldReturnValidToken() {
        String token = jwtUtil.generateToken(TEST_USERNAME);
        assertNotNull(token);
    }

    @Test
    @DisplayName("extractUsername returns correct username")
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtUtil.generateToken(TEST_USERNAME);
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(TEST_USERNAME, extractedUsername);
    }

    @Test
    @DisplayName("validateToken returns true for valid token")
    void validateToken_shouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken(TEST_USERNAME);
        boolean isValid = jwtUtil.validateToken(token, TEST_USERNAME);
        assertTrue(isValid);
    }

    @Test
    @DisplayName("extractAllClaims returns proper claims")
    void extractAllClaims_shouldReturnClaims() {
        String token = jwtUtil.generateToken(TEST_USERNAME);
        Jws<Claims> claims = jwtUtil.extractAllClaims(token);
        assertNotNull(claims);
        assertEquals(TEST_USERNAME, claims.getPayload().getSubject());
    }

    @Test
    @DisplayName("init throws exception when secret key is too short")
    void init_shouldThrowException_whenSecretKeyIsTooShort() {
        JwtUtil shortKeyJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(shortKeyJwtUtil, "secretKeyString", "short_key");
        assertThrows(IllegalArgumentException.class, shortKeyJwtUtil::init);
    }
}
