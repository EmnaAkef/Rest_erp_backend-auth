package com.rest_erp.backend_bi_rest_erp.auth.repository;

import com.rest_erp.backend_bi_rest_erp.auth.entity.UserRegistrationRequest;
import com.rest_erp.backend_bi_rest_erp.auth.entity.UserRegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRegistrationRequestRepository extends JpaRepository<UserRegistrationRequest, Long> {

    Optional<UserRegistrationRequest> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<UserRegistrationRequest> findByStatusOrderByCreatedAtDesc(UserRegistrationStatus status);

    boolean existsByEmailIgnoreCaseAndStatusIn(
            String email,
            List<UserRegistrationStatus> statuses
    );

    List<UserRegistrationRequest> findAllByOrderByCreatedAtDesc();

    List<UserRegistrationRequest> findByCompanyKeyOrderByCreatedAtDesc(Integer companyKey);

    List<UserRegistrationRequest> findByCompanyKeyAndStatusOrderByCreatedAtDesc(
            Integer companyKey,
            UserRegistrationStatus status
    );
}
