package com.spring.security.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

  private final JavaMailSender mailSender;

  @Async
  @EventListener
  public void sendWelcomeEmail(UserEvent event) {
    try {
      var dto = event.getUserResponseDto();
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(dto.getEmail());
      message.setSubject("Bem-vindo ao Sistema!");
      message.setText(
          String.format(
              "Olá %s,%n%nObrigado por se registrar em nosso sistema!", dto.getUsername()));
      mailSender.send(message);
      log.info("E-mail de boas-vindas enviado para {}", dto.getEmail());
    } catch (Exception ex) {
      log.error("Falha ao enviar e-mail de boas-vindas", ex);
      throw ex;
    }
  }

  @Async
  public void sendResetEmail(String email, String token) {
    try {
      String resetLink =
          UriComponentsBuilder.fromUriString("http://localhost:8080")
              .path("/api/v1/auth/reset-password")
              .queryParam("token", token)
              .toUriString();

      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(email);
      message.setSubject("Recuperação de Senha");
      message.setText("Clique no link para redefinir sua senha: " + resetLink);
      mailSender.send(message);
      log.info("E-mail de recuperação enviado para {}", email);
    } catch (Exception ex) {
      log.error("Falha ao enviar e-mail de recuperação para {}", email, ex);
      throw ex;
    }
  }
}
