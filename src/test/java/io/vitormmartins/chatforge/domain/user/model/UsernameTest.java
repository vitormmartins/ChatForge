package io.vitormmartins.chatforge.domain.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsernameTest {

  @Test
  void createsValidUsername_andTrimsWhitespace() {
    Username u = new Username("  alice  ");
    assertEquals("alice", u.value());
    assertEquals("alice", u.toString());
  }

  @Test
  void allowsMinAndMaxLength() {
    Username min = new Username("abc");
    assertEquals(3, min.value().length());

    String fifty = "a".repeat(50);
    Username max = new Username(fifty);
    assertEquals(50, max.value().length());
  }

  @Test
  void rejectsNull() {
    assertThrows(NullPointerException.class, () -> new Username(null));
  }

  @Test
  void rejectsEmptyOrWhitespace() {
    assertThrows(IllegalArgumentException.class, () -> new Username("   "));
    assertThrows(IllegalArgumentException.class, () -> new Username(""));
  }

  @Test
  void rejectsTooShortAndTooLong() {
    assertThrows(IllegalArgumentException.class, () -> new Username("ab")); // too short
    String tooLong = "x".repeat(51);
    assertThrows(IllegalArgumentException.class, () -> new Username(tooLong)); // too long
  }
}