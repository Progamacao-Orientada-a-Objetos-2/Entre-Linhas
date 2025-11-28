package com.entrelinhas.backend.repository;

import com.entrelinhas.backend.domain.TokenType;
import com.entrelinhas.backend.domain.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    Optional<UserToken> findByTokenAndTypeAndUsedFalseAndExpiresAtAfter(String token, TokenType type, LocalDateTime now);
}
