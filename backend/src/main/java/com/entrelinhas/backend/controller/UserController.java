package com.entrelinhas.backend.controller;

import com.entrelinhas.backend.domain.UserType;
import com.entrelinhas.backend.dto.user.AvailabilityUpdateRequest;
import com.entrelinhas.backend.dto.user.UserProfileResponse;
import com.entrelinhas.backend.dto.user.UserSearchResponse;
import com.entrelinhas.backend.dto.user.UserUpdateRequest;
import com.entrelinhas.backend.security.UserPrincipal;
import com.entrelinhas.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> profile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfile(id));
    }

    @GetMapping
    public ResponseEntity<List<UserSearchResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UserType userType,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String mainService,
            @RequestParam(required = false) Boolean availability
    ) {
        return ResponseEntity.ok(userService.search(name, userType, state, city, mainService, availability));
    }

    @PatchMapping("/me/availability")
    public ResponseEntity<Void> updateAvailability(@AuthenticationPrincipal UserPrincipal principal,
                                                   @Valid @RequestBody AvailabilityUpdateRequest request) {
        userService.updateAvailability(principal.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(@AuthenticationPrincipal UserPrincipal principal,
                                                             @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(principal.getId(), request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserPrincipal principal) {
        userService.deleteAccount(principal.getId());
        return ResponseEntity.noContent().build();
    }
}
