package com.entrelinhas.backend.dto.user;

import com.entrelinhas.backend.domain.UserType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSearchResponse {
    private final Long id;
    private final String fullName;
    private final UserType userType;
    private final String description;
    private final String state;
    private final String city;
    private final String mainService;
    private final Boolean availability;
    private final Double averageRating;
    private final Long serviceCount;
    private final Long reviewCount;
}
