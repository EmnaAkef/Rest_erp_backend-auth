package com.rest_erp.backend_bi_rest_erp.auth.repository;

import com.rest_erp.backend_bi_rest_erp.auth.entity.AppUserCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserCompanyRepository extends JpaRepository<AppUserCompany, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<AppUserCompany> findByEmailIgnoreCase(String email);

    Optional<AppUserCompany> findByKeycloakUserId(String keycloakUserId);
}