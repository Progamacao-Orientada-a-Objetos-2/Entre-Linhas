package com.entrelinhas.backend.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailabilityUpdateRequest {
    @NotNull
    private Boolean availability;
}
