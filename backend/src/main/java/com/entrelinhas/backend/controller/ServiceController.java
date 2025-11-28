package com.entrelinhas.backend.controller;

import com.entrelinhas.backend.dto.service.ServiceResponse;
import com.entrelinhas.backend.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceRepository serviceRepository;

    @GetMapping
    public ResponseEntity<List<ServiceResponse>> list() {
        var services = serviceRepository.findAll(Sort.by("name"));
        var result = services.stream()
                .map(s -> ServiceResponse.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .description(s.getDescription())
                        .parentId(s.getParentService() != null ? s.getParentService().getId() : null)
                        .build())
                .toList();
        return ResponseEntity.ok(result);
    }
}
