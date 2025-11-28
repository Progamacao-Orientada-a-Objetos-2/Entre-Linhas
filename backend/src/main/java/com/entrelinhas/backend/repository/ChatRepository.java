package com.entrelinhas.backend.repository;

import com.entrelinhas.backend.domain.Chat;
import com.entrelinhas.backend.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findBySenderOrReceiver(UserAccount sender, UserAccount receiver);

    Optional<Chat> findBySenderAndReceiver(UserAccount sender, UserAccount receiver);

    Optional<Chat> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
}
