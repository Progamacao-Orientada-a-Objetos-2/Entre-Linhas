package com.entrelinhas.backend.repository;

import com.entrelinhas.backend.domain.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    Optional<ServiceEntity> findByNameIgnoreCase(String name);
}
