package com.entrelinhas.backend.dto.auth;

import com.entrelinhas.backend.domain.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterRequest {

    @NotNull
    private UserType userType;

    @NotBlank
    private String fullName;

    private String cpf;

    private LocalDate birthDate;

    private String cnpj;

    private String stateRegistration;

    private String profileImageUrl;

    private String description;

    @Email
    @NotBlank
    private String email;

    @Size(min = 8)
    private String password;

    private String phone;

    private String state;

    private String city;

    private Long mainServiceId;
}
