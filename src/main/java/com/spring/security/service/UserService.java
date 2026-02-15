package com.spring.security.service;

import com.spring.security.dto.UserRequestDTO;
import com.spring.security.dto.UserResponseDTO;
import com.spring.security.entity.User;
import com.spring.security.exception.BusinessException;
import com.spring.security.listener.UserEvent;
import com.spring.security.security.Role;
import com.spring.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public UserResponseDTO create(UserRequestDTO request) {
    validateRequest(request);

    var user = toEntity(request);
    var savedUser = userRepository.save(user);
    var response = toResponse(savedUser);

    try {
      eventPublisher.publishEvent(new UserEvent(this, response));
    } catch (Exception e) {
      log.error("Erro ao publicar evento de criação de usuário: {}", e.getMessage());
    }

    return response;
  }

  private void validateRequest(UserRequestDTO request) {
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new BusinessException("Username already exists: " + request.getUsername());
    }

    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new BusinessException("Email already exists: " + request.getEmail());
    }
  }

  private User toEntity(UserRequestDTO request) {
    return User.builder()
        .username(request.getUsername())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.USER)
        .build();
  }

  private UserResponseDTO toResponse(User savedUser) {
    return UserResponseDTO.builder()
        .id(savedUser.getId())
        .username(savedUser.getUsername())
        .email(savedUser.getEmail())
        .role(savedUser.getRole())
        .createdAt(savedUser.getCreatedAt())
        .updatedAt(savedUser.getUpdatedAt())
        .build();
  }
}
