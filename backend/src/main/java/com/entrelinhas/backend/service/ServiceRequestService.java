package com.entrelinhas.backend.service;

import com.entrelinhas.backend.domain.ServiceRequest;
import com.entrelinhas.backend.domain.ServiceRequestStatus;
import com.entrelinhas.backend.domain.UserAccount;
import com.entrelinhas.backend.domain.UserType;
import com.entrelinhas.backend.dto.service.ServiceRequestCreate;
import com.entrelinhas.backend.dto.service.ServiceRequestResponse;
import com.entrelinhas.backend.repository.ServiceRepository;
import com.entrelinhas.backend.repository.ServiceRequestRepository;
import com.entrelinhas.backend.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final UserAccountRepository userAccountRepository;
    private final ServiceRepository serviceRepository;

    @Transactional
    public ServiceRequestResponse create(ServiceRequestCreate request, UserAccount company) {
        if (company.getUserType() != UserType.COMPANY) {
            throw new IllegalArgumentException("Somente empresas podem criar solicitações");
        }
        UserAccount faction = userAccountRepository.findById(request.getFactionId())
                .orElseThrow(() -> new IllegalArgumentException("Facção não encontrada"));
        if (faction.getUserType() != UserType.FACTION) {
            throw new IllegalArgumentException("Destinatário não é uma facção");
        }

        ServiceRequest entity = ServiceRequest.builder()
                .name(request.getName())
                .description(request.getDescription())
                .company(company)
                .faction(faction)
                .service(serviceRepository.findById(request.getServiceId()).orElseThrow())
                .status(ServiceRequestStatus.PENDING)
                .build();
        serviceRequestRepository.save(entity);
        return toResponse(entity);
    }

    @Transactional
    public ServiceRequestResponse accept(Long id, UserAccount faction) {
        ServiceRequest request = serviceRequestRepository.findById(id).orElseThrow();
        if (!request.getFaction().getId().equals(faction.getId())) {
            throw new IllegalArgumentException("Apenas a facção responsável pode aceitar");
        }
        request.setStatus(ServiceRequestStatus.ACCEPTED);
        serviceRequestRepository.save(request);
        return toResponse(request);
    }

    @Transactional
    public ServiceRequestResponse reject(Long id, UserAccount faction) {
        ServiceRequest request = serviceRequestRepository.findById(id).orElseThrow();
        if (!request.getFaction().getId().equals(faction.getId())) {
            throw new IllegalArgumentException("Apenas a facção responsável pode rejeitar");
        }
        request.setStatus(ServiceRequestStatus.REJECTED);
        serviceRequestRepository.save(request);
        return toResponse(request);
    }

    @Transactional(readOnly = true)
    public List<ServiceRequestResponse> listFor(UserAccount user) {
        List<ServiceRequest> requests = serviceRequestRepository.findByCompanyOrFaction(user, user);
        return requests.stream().map(this::toResponse).toList();
    }

    private ServiceRequestResponse toResponse(ServiceRequest request) {
        return ServiceRequestResponse.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .companyId(request.getCompany().getId())
                .factionId(request.getFaction().getId())
                .serviceId(request.getService().getId())
                .serviceName(request.getService().getName())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
