package io.vitormmartins.chatforge.spring_chatforge.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.vitormmartins.chatforge.spring_chatforge.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JwtFilterTest {

  public static final int EXPIRATION_INTERVAL_MILLIS = 1000 * 60 * 60 * 2;
  @Value("${jwt.secret-key}")
  private String secretKeyString = "a_very_secret_key_that_is_at_least_32_characters_long";

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private UserDetailsService userDetailsService;

  @InjectMocks
  private JwtFilter jwtFilter;

  @BeforeEach
  public void setUp() {
    try (AutoCloseable ignored = MockitoAnnotations.openMocks(this)) {
      // Initialization code if needed
      jwtUtil = new JwtUtil();
      ReflectionTestUtils.setField(jwtUtil, "secretKeyString", secretKeyString);
      jwtUtil.init();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void generateToken_shouldReturnValidToken() {
    String token = jwtUtil.generateToken("testuser");
    assertNotNull(token);
  }

  @Test
  void extractAllClaims_shouldReturnClaims_whenTokenIsValid() {
    String token = jwtUtil.generateToken("testuser");
    Jws<Claims> claims = jwtUtil.extractAllClaims(token);
    assertEquals("testuser", claims.getPayload().getSubject());
  }

  @Test
  void extractAllClaims_shouldThrowException_whenTokenIsExpired() {
    String expiredToken = Jwts.builder()
                              .subject("testuser")
                              .issuedAt(new Date(System.currentTimeMillis() - EXPIRATION_INTERVAL_MILLIS))
                              .expiration(new Date(System.currentTimeMillis() - EXPIRATION_INTERVAL_MILLIS))
                              .signWith(Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8)))
                              .compact();

    assertThrows(RuntimeException.class, () -> jwtUtil.extractAllClaims(expiredToken));
  }

  @Test
  void extractAllClaims_shouldThrowException_whenTokenIsInvalid() {
    String invalidToken = "invalid.token.here";
    assertThrows(RuntimeException.class, () -> jwtUtil.extractAllClaims(invalidToken));
  }

  @Test
  void init_shouldThrowException_whenSecretKeyIsTooShort() {
    JwtUtil shortKeyJwtUtil = new JwtUtil();
    ReflectionTestUtils.setField(shortKeyJwtUtil, "secretKeyString", "short_key");
    assertThrows(IllegalArgumentException.class, shortKeyJwtUtil::init);
  }
}
