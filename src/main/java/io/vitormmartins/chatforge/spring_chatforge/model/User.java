package io.vitormmartins.chatforge.spring_chatforge.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Getter
  @Column(unique = true)
  private String username;

  @Getter
  private String password;

}

