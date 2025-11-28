package com.entrelinhas.backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserUpdateRequest {

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    private String phone;

    private String state;

    private String city;

    private String description;

    private Long mainServiceId;

    private Boolean availability;

    private String cpf;

    private LocalDate birthDate;

    private String cnpj;

    private String stateRegistration;
}
