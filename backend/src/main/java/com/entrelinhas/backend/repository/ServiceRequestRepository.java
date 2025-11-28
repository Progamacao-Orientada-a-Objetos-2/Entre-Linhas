package com.entrelinhas.backend.repository;

import com.entrelinhas.backend.domain.ServiceRequest;
import com.entrelinhas.backend.domain.UserAccount;
import com.entrelinhas.backend.domain.ServiceRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByCompanyOrFaction(UserAccount company, UserAccount faction);

    long countByCompanyAndStatusIn(UserAccount company, Collection<ServiceRequestStatus> statuses);

    long countByFactionAndStatusIn(UserAccount faction, Collection<ServiceRequestStatus> statuses);
}
