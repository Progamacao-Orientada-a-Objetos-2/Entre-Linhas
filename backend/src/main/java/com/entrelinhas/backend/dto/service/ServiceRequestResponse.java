package com.entrelinhas.backend.dto.service;

import com.entrelinhas.backend.domain.ServiceRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ServiceRequestResponse {
    private final Long id;
    private final String name;
    private final String description;
    private final Long companyId;
    private final Long factionId;
    private final Long serviceId;
    private final String serviceName;
    private final ServiceRequestStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
