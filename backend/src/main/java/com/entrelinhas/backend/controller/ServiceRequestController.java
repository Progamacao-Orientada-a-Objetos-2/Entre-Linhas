package com.entrelinhas.backend.controller;

import com.entrelinhas.backend.dto.service.ServiceRequestCreate;
import com.entrelinhas.backend.dto.service.ServiceRequestResponse;
import com.entrelinhas.backend.security.UserPrincipal;
import com.entrelinhas.backend.service.ServiceRequestService;
import com.entrelinhas.backend.repository.UserAccountRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;
    private final UserAccountRepository userAccountRepository;

    @PostMapping
    public ResponseEntity<ServiceRequestResponse> create(@AuthenticationPrincipal UserPrincipal principal,
                                                         @Valid @RequestBody ServiceRequestCreate request) {
        return ResponseEntity.ok(serviceRequestService.create(request, userAccountRepository.findById(principal.getId()).orElseThrow()));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<ServiceRequestResponse> accept(@PathVariable Long id,
                                                         @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(serviceRequestService.accept(id, userAccountRepository.findById(principal.getId()).orElseThrow()));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ServiceRequestResponse> reject(@PathVariable Long id,
                                                         @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(serviceRequestService.reject(id, userAccountRepository.findById(principal.getId()).orElseThrow()));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ServiceRequestResponse>> mine(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(serviceRequestService.listFor(userAccountRepository.findById(principal.getId()).orElseThrow()));
    }
}
