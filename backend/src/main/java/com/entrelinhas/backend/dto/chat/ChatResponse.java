package com.entrelinhas.backend.dto.chat;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatResponse {
    private final Long id;
    private final Long otherUserId;
    private final String otherUserName;
    private final LocalDateTime createdAt;
}
