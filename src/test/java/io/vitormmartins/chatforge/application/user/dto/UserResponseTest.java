package io.vitormmartins.chatforge.application.user.dto;

import io.vitormmartins.chatforge.domain.user.model.Email;
import io.vitormmartins.chatforge.domain.user.model.User;
import io.vitormmartins.chatforge.domain.user.model.Username;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseTest {

  @Test
  void from_withId_mapsAllFields() {
    LocalDateTime createdAt = LocalDateTime.of(2020, 1, 1, 12, 0);
    User user = new User(Optional.of(42L),
                         new Username("alice"),
                         new Email("alice@example.com"),
                         "secret",
                         createdAt,
                         createdAt);

    UserResponse resp = UserResponse.from(user);

    assertNotNull(resp);
    assertEquals(42L, resp.id());
    assertEquals("alice", resp.username());
    assertEquals("alice@example.com", resp.email());
    assertEquals(createdAt, resp.createdAt());
  }

  @Test
  void from_withoutId_mapsNullId() {
    LocalDateTime createdAt = LocalDateTime.of(2021, 6, 15, 8, 30);
    User user = new User(Optional.empty(),
                         new Username("bob"),
                         new Email("bob@example.com"),
                         "pwd",
                         createdAt,
                         createdAt);

    UserResponse resp = UserResponse.from(user);

    assertNotNull(resp);
    assertNull(resp.id());
    assertEquals("bob", resp.username());
    assertEquals("bob@example.com", resp.email());
    assertEquals(createdAt, resp.createdAt());
  }
}