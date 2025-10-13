package com.spring.security.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

  @NotEmpty(message = "Username must not be empty")
  private String username;

  @NotEmpty(message = "Password must not be empty")
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
      message =
          "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character")
  private String password;
}
