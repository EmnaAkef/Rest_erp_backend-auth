package com.rest_erp.backend_bi_rest_erp.auth.service;

import com.rest_erp.backend_bi_rest_erp.auth.entity.AppRole;
import com.rest_erp.backend_bi_rest_erp.auth.entity.AppUserCompany;
import com.rest_erp.backend_bi_rest_erp.auth.entity.UserRegistrationRequest;
import com.rest_erp.backend_bi_rest_erp.auth.entity.UserRegistrationStatus;
import com.rest_erp.backend_bi_rest_erp.auth.repository.AppUserCompanyRepository;
import com.rest_erp.backend_bi_rest_erp.auth.repository.UserRegistrationRequestRepository;
import com.rest_erp.backend_bi_rest_erp.company.dto.CompanyInfo;
import com.rest_erp.backend_bi_rest_erp.company.service.CompanyLookupService;
import org.springframework.stereotype.Service;
import com.rest_erp.backend_bi_rest_erp.auth.entity.AppRole;
import com.rest_erp.backend_bi_rest_erp.bi.tenant.TenantContext;

import java.util.Objects;

import java.util.List;

@Service
public class AdminRegistrationService {

    private final UserRegistrationRequestRepository requestRepository;
    private final AppUserCompanyRepository appUserCompanyRepository;
    private final CompanyLookupService companyLookupService;
    private final KeycloakService keycloakService;

    public AdminRegistrationService(
            UserRegistrationRequestRepository requestRepository,
            AppUserCompanyRepository appUserCompanyRepository,
            CompanyLookupService companyLookupService,
            KeycloakService keycloakService
    ) {
        this.requestRepository = requestRepository;
        this.appUserCompanyRepository = appUserCompanyRepository;
        this.companyLookupService = companyLookupService;
        this.keycloakService = keycloakService;
    }

    public List<UserRegistrationRequest> getAllRequests() {
        AppRole role = TenantContext.getUserRole();
        Integer companyKey = TenantContext.getCompanyKey();

        if (role == AppRole.SUPER_ADMIN) {
            return requestRepository.findAllByOrderByCreatedAtDesc();
        }

        if (role == AppRole.COMPANY_ADMIN) {
            if (companyKey == null) {
                throw new RuntimeException("Company key is missing for company admin");
            }

            return requestRepository.findByCompanyKeyOrderByCreatedAtDesc(companyKey);
        }

        throw new RuntimeException("Access denied: insufficient permissions");
    }

    public List<UserRegistrationRequest> getPendingRequests() {
        AppRole role = TenantContext.getUserRole();
        Integer companyKey = TenantContext.getCompanyKey();

        if (role == AppRole.SUPER_ADMIN) {
            return requestRepository.findByStatusOrderByCreatedAtDesc(UserRegistrationStatus.PENDING);
        }

        if (role == AppRole.COMPANY_ADMIN) {
            if (companyKey == null) {
                throw new RuntimeException("Company key is missing for company admin");
            }

            return requestRepository.findByCompanyKeyAndStatusOrderByCreatedAtDesc(
                    companyKey,
                    UserRegistrationStatus.PENDING
            );
        }

        throw new RuntimeException("Access denied: insufficient permissions");
    }

    public UserRegistrationRequest getRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration request not found"));
    }

    public String approveRequest(Long id) {
        UserRegistrationRequest request = getRequestById(id);
        checkAdminCanManageRequest(request);

        if (request.getStatus() == UserRegistrationStatus.APPROVED) {
            throw new RuntimeException("This request is already approved");
        }

        if (request.getStatus() == UserRegistrationStatus.REJECTED) {
            throw new RuntimeException("This request is already rejected");
        }

        if (request.getStatus() == UserRegistrationStatus.PENDING_COMPANY_NOT_FOUND) {
            throw new RuntimeException("Cannot approve this request because the company was not found");
        }

        if (appUserCompanyRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new RuntimeException("This email is already linked to a company");
        }

        CompanyInfo companyInfo = companyLookupService.findCompanyByName(request.getCompanyName());


        String keycloakUserId = keycloakService.createUser(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getCompanyName()
        );
        keycloakService.assignRealmRoleToUser(keycloakUserId, AppRole.USER.name());

        keycloakService.sendUpdatePasswordEmail(keycloakUserId);

        AppUserCompany appUserCompany = new AppUserCompany();
        appUserCompany.setKeycloakUserId(keycloakUserId);
        appUserCompany.setFirstName(request.getFirstName());
        appUserCompany.setLastName(request.getLastName());
        appUserCompany.setEmail(request.getEmail());
        appUserCompany.setCompanyId(companyInfo.getCompanyId());
        appUserCompany.setCompanyKey(companyInfo.getCompanyKey());
        appUserCompany.setCompanyName(companyInfo.getCompanyName());
        appUserCompany.setProfileImageUrl(request.getProfileImageUrl());
        appUserCompany.setRole(AppRole.USER);
        appUserCompany.setActive(true);

        appUserCompanyRepository.save(appUserCompany);

        request.setStatus(UserRegistrationStatus.APPROVED);
        request.setRejectionReason(null);

        requestRepository.save(request);

        return "Registration request approved successfully. User created in Keycloak.";
    }

    public String rejectRequest(Long id, String reason) {
        UserRegistrationRequest request = getRequestById(id);
        checkAdminCanManageRequest(request);

        if (request.getStatus() == UserRegistrationStatus.APPROVED) {
            throw new RuntimeException("Cannot reject an already approved request");
        }

        if (request.getStatus() == UserRegistrationStatus.REJECTED) {
            throw new RuntimeException("This request is already rejected");
        }

        request.setStatus(UserRegistrationStatus.REJECTED);

        if (reason != null && !reason.trim().isEmpty()) {
            request.setRejectionReason(reason.trim());
        } else {
            request.setRejectionReason("Rejected by admin");
        }

        requestRepository.save(request);

        return "Registration request rejected successfully";
    }

    private void checkAdminCanManageRequest(UserRegistrationRequest request) {
        AppRole role = TenantContext.getUserRole();
        Integer currentCompanyKey = TenantContext.getCompanyKey();

        if (role == AppRole.SUPER_ADMIN) {
            return;
        }

        if (role == AppRole.COMPANY_ADMIN) {
            if (currentCompanyKey == null) {
                throw new RuntimeException("Company key is missing for company admin");
            }

            if (!Objects.equals(currentCompanyKey, request.getCompanyKey())) {
                throw new RuntimeException("Access denied: you cannot manage requests from another company");
            }

            return;
        }

        throw new RuntimeException("Access denied: insufficient permissions");
    }


}