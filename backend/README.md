# Backend - Entre Linhas

Backend desenvolvido em **Java 21** com **Spring Boot**.



## Tecnologias

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL (desenvolvimento e produção)
- H2 Database (testes)

## Configuração rápida

- **Profiles**: o padrão é `dev` (`SPRING_PROFILES_ACTIVE` pode sobrescrever). O profile `test` usa PostgreSQL com Liquibase (mesmas credenciais do serviço do CI) e é o profile ativo no pipeline.
- **Banco (dev/test)**: por padrão aponta para `jdbc:postgresql://localhost:5432/entrelinhas` no `dev` e para `jdbc:postgresql://localhost:5432/testdb` no `test` (usuário/senha `test`). Ajuste `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER` e `DB_PASSWORD` conforme seu ambiente.
- **JWT**: defina `JWT_SECRET` com uma chave de no mínimo 32 bytes (UTF-8). Recomenda-se usar uma chave de 256 bits em Base64 ou uma frase longa aleatória. O tempo de expiração (segundos) pode ser alterado via `JWT_EXPIRATION_SECONDS`.
