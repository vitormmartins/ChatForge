package io.vitormmartins.chatforge.spring_chatforge.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@PropertySource("classpath:secrets.properties") // Load the properties file
public class JwtUtil {

  private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

  @Value("${jwt.secret-key:secret}")
  private String secretKeyString;

  private final byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
  private final SecretKey key = Keys.hmacShaKeyFor(keyBytes);
  private final JwtParser parser = Jwts.parser().verifyWith(key).build();
  private final JwtBuilder builder = Jwts.builder().signWith(key, SignatureAlgorithm.HS256);

  public String generateToken(String username) {
    return builder.claim("sub", username)
                  .setIssuedAt(new Date())
                  .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                  .compact();
  }

  public Jws<Claims> extractAllClaims(String token) {
    try {
      var claims = parser.parseSignedClaims(token);
      if (!claims.getPayload().getExpiration().before(new Date())) {
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

