# API Spring Security - JWT

API REST em Java com Spring Boot, Spring Security, autenticação com JWT, envio de e-mails e recuperação de senha.

## Tecnologias

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Spring Mail
- JWT
- Maven

## Requisitos

- Java 17\+
- Maven 3\.9\+
- Banco de dados configurado em `src/main/resources/application.properties`

## Configuração

Propriedades relevantes em `application.properties`:

- Configuração SMTP:
    - `spring.mail.host`,
    - `spring.mail.port`
    - `spring.mail.username`, // usuário do e-mail
    - `spring.mail.password`  // senha de app do e-mail
    - `spring.mail.properties.mail.smtp.auth` \= true
    - `spring.mail.properties.mail.smtp.starttls.enable` \= true

Senhas são codificadas com BCrypt.

## Executando

- Via Maven: `mvn spring-boot:run`
- Build: `mvn clean package`
- JAR: `java -jar target/<seu-jar>.jar`

## Autenticação

- Obtenha um JWT em `POST /api/v1/auth/login`
- Envie o token no header: `Authorization: Bearer <token>`

## Endpoints

Rotas públicas:

- `POST /api/v1/users/register` \- cadastra usuário
    - Body exemplo:
      ```json
      { "username": "alice", "email": "alice@example.com", "password": "S3nh@Fort3!" }
      ```
    - A senha deve ter: 8\+ chars, maiúscula, minúscula, dígito e caractere especial.

- `POST /api/v1/auth/login` \- autentica e retorna JWT
    - Body:
      ```json
      { "username": "alice", "password": "S3nh@Fort3!" }
      ```
    - Resposta esperada: JWT no corpo ou header (conforme implementação do serviço).

- `POST /api/v1/auth/request-reset-password` \- envia e\-mail de recuperação
    - Body:
      ```json
      { "email": "alice@example.com" }
      ```

- `POST /api/v1/auth/reset-password` \- redefine a senha
    - Body:
      ```json
      { "token": "<token recebido por e-mail>", "newPassword": "Nov@S3nh4!" }
      ```