package com.entrelinhas.backend.dto.review;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {
    private final Long id;
    private final Long senderId;
    private final Long receiverId;
    private final Long serviceRequestId;
    private final Short rating;
    private final String comment;
    private final LocalDateTime createdAt;
}
