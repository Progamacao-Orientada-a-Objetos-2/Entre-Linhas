package com.entrelinhas.backend.dto.user;

import com.entrelinhas.backend.domain.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserSummary {
    private final Long id;
    private final String fullName;
    private final UserType userType;
    private final String email;
}
