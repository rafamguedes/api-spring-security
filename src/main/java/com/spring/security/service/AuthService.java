package com.spring.security.service;

import com.spring.security.dto.LoginRequestDTO;
import com.spring.security.dto.LoginResponseDTO;
import com.spring.security.entity.Token;
import com.spring.security.exception.BusinessException;
import com.spring.security.repository.TokenRepository;
import com.spring.security.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
  private static final String USER_NOT_FOUND_BY_EMAIL = "User not found for email: ";
  private static final String TOKEN_NOT_FOUND = "Not found token for password reset";
  private static final String INVALID_EMAIL_OR_PASSWORD = "Invalid email or password";
  private static final String EXPIRED_TOKEN = "Expired token, please request a new password reset";
  private static final String INTERNAL_ERROR = "Internal Error occurred";

  private final TokenService tokenService;
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final AuthenticationManager authenticationManager;

  public LoginResponseDTO authenticate(LoginRequestDTO login) {
    try {
      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());

      Authentication auth = authenticationManager.authenticate(authToken);

      String token = tokenService.generateToken(auth.getName());

      return new LoginResponseDTO(token);
    } catch (BadCredentialsException e) {
      throw new BadCredentialsException(INVALID_EMAIL_OR_PASSWORD, e);
    } catch (Exception e) {
      throw new RuntimeException(INTERNAL_ERROR, e);
    }
  }

  public void requestPasswordReset(String email) {
    var user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_BY_EMAIL + email));

    var resetToken =
        Token.builder()
            .token(UUID.randomUUID().toString())
            .user(user)
            .expirationDate(LocalDateTime.now().plusHours(1))
            .build();

    tokenRepository.save(resetToken);
  }

  public void passwordReset(String token, String newPassword) {
    var resetToken =
        tokenRepository
            .findByToken(token)
            .orElseThrow(() -> new EntityNotFoundException(TOKEN_NOT_FOUND));

    if (resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
      throw new BusinessException(EXPIRED_TOKEN);
    }

    var user = resetToken.getUser();
    user.setPassword(new BCryptPasswordEncoder().encode(newPassword));

    userRepository.save(user);
    tokenRepository.delete(resetToken);
  }
}
