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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final ApplicationEventPublisher eventPublisher;

  public UserResponseDTO create(UserRequestDTO request) {
    validateRequest(request);

    var user = toEntity(request);
    var savedUser = toResponse(userRepository.save(user));

    // Sending welcome email
    eventPublisher.publishEvent(new UserEvent(this, savedUser));

    return savedUser;
  }

  private void validateRequest(UserRequestDTO request) {
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new BusinessException("Username already exists: " + request.getUsername());
    }

    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new BusinessException("Email already exists: " + request.getEmail());
    }
  }

  private static User toEntity(UserRequestDTO request) {
    return User.builder()
        .username(request.getUsername())
        .email(request.getEmail())
        .password(new BCryptPasswordEncoder().encode(request.getPassword()))
        .role(Role.USER)
        .build();
  }

  private static UserResponseDTO toResponse(User savedUser) {
    return UserResponseDTO.builder()
        .id(savedUser.getId())
        .username(savedUser.getUsername())
        .email(savedUser.getEmail())
        .role(savedUser.getRole())
        .createdAt(savedUser.getCreatedAt())
        .updatedAt(savedUser.getUpdatedAt())
        .build();
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByUsername(username)
        .orElseThrow(
            () -> new UsernameNotFoundException("User not found by username: " + username));
  }
}
