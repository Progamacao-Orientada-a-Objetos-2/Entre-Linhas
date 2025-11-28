package com.entrelinhas.backend.dto.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceRequestCreate {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Long factionId;

    @NotNull
    private Long serviceId;
}
