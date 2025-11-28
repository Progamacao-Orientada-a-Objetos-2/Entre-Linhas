package com.entrelinhas.backend.dto.chat;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MessageResponse {
    private final Long id;
    private final Long senderId;
    private final String content;
    private final LocalDateTime createdAt;
    private final boolean read;
}
