package io.vitormmartins.chatforge.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PasswordConfig Test")
class PasswordConfigTest {

  @Test
  @DisplayName("Test passwordEncoder bean creation returns a BCryptPasswordEncoder instance")
  void testPasswordEncoder() {
    PasswordConfig config = new PasswordConfig();
    PasswordEncoder encoder = config.passwordEncoder();
    assertNotNull(encoder, "PasswordEncoder should not be null");
    assertInstanceOf(BCryptPasswordEncoder.class,
                     encoder,
                     "PasswordEncoder should be an instance of BCryptPasswordEncoder");
  }
}