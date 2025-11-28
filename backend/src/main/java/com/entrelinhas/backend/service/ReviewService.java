package com.entrelinhas.backend.service;

import com.entrelinhas.backend.domain.Review;
import com.entrelinhas.backend.domain.ServiceRequest;
import com.entrelinhas.backend.domain.ServiceRequestStatus;
import com.entrelinhas.backend.domain.UserAccount;
import com.entrelinhas.backend.dto.review.ReviewRequest;
import com.entrelinhas.backend.dto.review.ReviewResponse;
import com.entrelinhas.backend.repository.ReviewRepository;
import com.entrelinhas.backend.repository.ServiceRequestRepository;
import com.entrelinhas.backend.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserAccountRepository userAccountRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    @Transactional
    public ReviewResponse create(ReviewRequest request, UserAccount sender) {
        if (sender.getId().equals(request.getReceiverId())) {
            throw new IllegalArgumentException("Não é permitido avaliar a si mesmo");
        }
        UserAccount receiver = userAccountRepository.findById(request.getReceiverId()).orElseThrow();
        ServiceRequest serviceRequest = serviceRequestRepository.findById(request.getServiceRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));
        validateServiceRequestForReview(sender, receiver, serviceRequest);

        Review review = reviewRepository.findBySenderAndReceiverAndServiceRequest(sender, receiver, serviceRequest)
                .orElse(Review.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .serviceRequest(serviceRequest)
                        .build());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        reviewRepository.save(review);
        return toResponse(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> listFor(Long receiverId) {
        UserAccount receiver = userAccountRepository.findById(receiverId).orElseThrow();
        return reviewRepository.findByReceiver(receiver).stream().map(this::toResponse).toList();
    }

    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .senderId(review.getSender().getId())
                .receiverId(review.getReceiver().getId())
                .serviceRequestId(review.getServiceRequest().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private void validateServiceRequestForReview(UserAccount sender, UserAccount receiver, ServiceRequest serviceRequest) {
        if (serviceRequest.getStatus() != ServiceRequestStatus.ACCEPTED) {
            throw new IllegalArgumentException("Avaliações só são permitidas após conclusão do serviço");
        }
        boolean matchesParticipants = (serviceRequest.getCompany().getId().equals(sender.getId()) && serviceRequest.getFaction().getId().equals(receiver.getId()))
                || (serviceRequest.getFaction().getId().equals(sender.getId()) && serviceRequest.getCompany().getId().equals(receiver.getId()));
        if (!matchesParticipants) {
            throw new IllegalArgumentException("Avaliação não corresponde aos participantes da solicitação");
        }
    }
}
