package com.entrelinhas.backend.controller;

import com.entrelinhas.backend.dto.review.ReviewRequest;
import com.entrelinhas.backend.dto.review.ReviewResponse;
import com.entrelinhas.backend.repository.UserAccountRepository;
import com.entrelinhas.backend.security.UserPrincipal;
import com.entrelinhas.backend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserAccountRepository userAccountRepository;

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@AuthenticationPrincipal UserPrincipal principal,
                                                 @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.create(request, userAccountRepository.findById(principal.getId()).orElseThrow()));
    }

    @GetMapping("/{receiverId}")
    public ResponseEntity<List<ReviewResponse>> list(@PathVariable Long receiverId) {
        return ResponseEntity.ok(reviewService.listFor(receiverId));
    }
}
