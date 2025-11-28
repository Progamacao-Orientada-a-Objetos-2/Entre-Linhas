package com.entrelinhas.backend.repository;

import com.entrelinhas.backend.domain.Chat;
import com.entrelinhas.backend.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatOrderByCreatedAtAsc(Chat chat);
}
