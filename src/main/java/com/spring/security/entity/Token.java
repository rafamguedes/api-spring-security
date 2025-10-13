package com.spring.security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_token")
@EqualsAndHashCode(callSuper = true)
public class Token extends Generic {

  @Column(name = "token")
  private String token;

  @Column(name = "expiration_date")
  private LocalDateTime expirationDate;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
