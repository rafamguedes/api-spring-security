package com.spring.security.controller;

import com.spring.security.dto.LoginRequestDTO;
import com.spring.security.dto.LoginResponseDTO;
import com.spring.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO login) {
    var response = authService.authenticate(login);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/request-reset-password")
  public ResponseEntity<Void> requestPasswordReset(@RequestParam String email) {
    authService.requestPasswordReset(email);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Void> resetPassword(
      @RequestParam String token, @RequestParam String newPassword) {
    authService.passwordReset(token, newPassword);
    return ResponseEntity.ok().build();
  }
}
