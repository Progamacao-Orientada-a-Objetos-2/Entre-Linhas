package com.entrelinhas.backend.dto.user;

import com.entrelinhas.backend.domain.UserType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserProfileResponse {
    private final Long id;
    private final UserType userType;
    private final String fullName;
    private final String cpf;
    private final LocalDate birthDate;
    private final String cnpj;
    private final String stateRegistration;
    private final String profileImageUrl;
    private final String description;
    private final String email;
    private final boolean emailVerified;
    private final String phone;
    private final String state;
    private final String city;
    private final String mainService;
    private final Long mainServiceId;
    private final Boolean availability;
    private final Double averageRating;
    private final Long serviceCount;
    private final Long reviewCount;
    private final LocalDateTime lastAccessAt;
}
