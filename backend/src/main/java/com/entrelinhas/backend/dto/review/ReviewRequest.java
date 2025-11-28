package com.entrelinhas.backend.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {
    @NotNull
    private Long receiverId;

    @NotNull
    private Long serviceRequestId;

    @NotNull
    @Min(1)
    @Max(5)
    private Short rating;

    private String comment;
}
