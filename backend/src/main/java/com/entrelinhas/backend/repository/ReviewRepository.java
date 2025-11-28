package com.entrelinhas.backend.repository;

import com.entrelinhas.backend.domain.Review;
import com.entrelinhas.backend.domain.UserAccount;
import com.entrelinhas.backend.domain.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReceiver(UserAccount receiver);

    Optional<Review> findBySenderAndReceiverAndServiceRequest(UserAccount sender, UserAccount receiver, ServiceRequest serviceRequest);

    @Query("select avg(r.rating) from Review r where r.receiver = :receiver")
    Double calculateAverage(UserAccount receiver);

    long countByReceiver(UserAccount receiver);
}
