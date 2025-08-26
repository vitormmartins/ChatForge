package io.vitormmartins.chatforge.infrastructure.persistence.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA entity for User persistence.
 * This is an infrastructure concern, separate from the domain model.
 */
@Setter
@Getter
@Entity
@Table(name = "users")
public class UserJpaEntity {

  // Getters and setters
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(unique = true)
  private String email;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // Default constructor for JPA
  protected UserJpaEntity() {
  }

  public UserJpaEntity(String username,
                       String password,
                       String email,
                       LocalDateTime createdAt,
                       LocalDateTime updatedAt) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
    if (updatedAt == null) {
      updatedAt = LocalDateTime.now();
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

}
