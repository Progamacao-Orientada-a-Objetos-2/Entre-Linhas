package com.entrelinhas.backend.repository;

import com.entrelinhas.backend.domain.UserAccount;
import com.entrelinhas.backend.domain.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long>, JpaSpecificationExecutor<UserAccount> {
    Optional<UserAccount> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByCnpj(String cnpj);

    boolean existsByStateRegistration(String stateRegistration);

    boolean existsByPhone(String phone);

    long countByUserType(UserType userType);
}
