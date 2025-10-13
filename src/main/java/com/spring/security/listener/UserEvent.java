package com.spring.security.listener;

import com.spring.security.dto.UserResponseDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserEvent extends ApplicationEvent {
  private final UserResponseDTO userResponseDto;

  public UserEvent(Object source, UserResponseDTO userResponseDto) {
    super(source);
    this.userResponseDto = userResponseDto;
  }
}
