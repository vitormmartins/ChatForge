package io.vitormmartins.chatforge.domain.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

  @Test
  void testToString() {
    Email e = new Email("user+tag@example.com");
    assertEquals("user+tag@example.com", e.toString());
  }

  @Test
  void value() {
    Email e = new Email("nome.sobrenome@sub.exemplo.org");
    assertEquals("nome.sobrenome@sub.exemplo.org", e.value());
  }

  @Test
  void invalidEmailThrows() {
    assertThrows(IllegalArgumentException.class, () -> new Email("invalid-email"));
    assertThrows(IllegalArgumentException.class, () -> new Email("no-at-sign.com"));
    assertThrows(IllegalArgumentException.class, () -> new Email("user@.com"));
  }

  @Test
  void nullEmailThrows() {
    NullPointerException ex = assertThrows(NullPointerException.class, () -> new Email(null));
    assertTrue(ex.getMessage().contains("Email cannot be null"));
  }
}