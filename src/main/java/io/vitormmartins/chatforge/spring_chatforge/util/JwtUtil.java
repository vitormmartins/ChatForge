package io.vitormmartins.chatforge.spring_chatforge.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
  private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

  @Value("${jwt.secret-key}")
  private String secretKeyString;

  private JwtParser parser;
  private JwtBuilder builder;

  @PostConstruct
  public void init() {
    // Validate secret key before using it
    if (secretKeyString == null || secretKeyString.length() < 32) {
      throw new IllegalArgumentException("Secret key is too short!");
    }

    // Initialize JWT components after validation
    byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
    SecretKey key = Keys.hmacShaKeyFor(keyBytes);
    builder = Jwts.builder().signWith(key);
    parser = Jwts.parser().verifyWith(key).build();
  }

  public String generateToken(String username) {
    return builder.claim("sub", username)
                  .issuedAt(new Date(System.currentTimeMillis()))
                  .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                  .compact();
  }

  public Jws<Claims> extractAllClaims(String token) {
    try {
      var claims = parser.parseSignedClaims(token);
      if (claims.getPayload().getExpiration().before(new Date(System.currentTimeMillis()))) {
        throw new RuntimeException("Token expired");
      }
      return claims;
    } catch (JwtException e) {
      throw new RuntimeException("Invalid token");
    }
  }

  @PostConstruct
  public void validateSecretKey() {
    if (secretKeyString == null || secretKeyString.length() < 32) {
      throw new IllegalArgumentException("Secret key is too short!");
    }
  }

}

