package com.spring.security.audit;

import com.spring.security.entity.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareUser implements AuditorAware<String> {

  @Override
  @NonNull
  public Optional<String> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return Optional.of("SYSTEM");
    }

    Object principal = authentication.getPrincipal();

    if (principal instanceof String && "anonymousUser".equals(principal)) {
      return Optional.of("ANONYMOUS");
    }

    if (principal instanceof User) {
      User user = (User) principal;
      return Optional.of(user.getEmail());
    }

    if (principal instanceof String) {
      return Optional.of((String) principal);
    }

    return Optional.of("UNKNOWN");
  }
}
