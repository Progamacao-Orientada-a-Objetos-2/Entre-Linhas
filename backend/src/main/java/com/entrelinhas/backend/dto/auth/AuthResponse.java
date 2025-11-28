package com.entrelinhas.backend.dto.auth;

import com.entrelinhas.backend.dto.user.UserSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AuthResponse {
    private final String accessToken;
    private final UserSummary user;
}
