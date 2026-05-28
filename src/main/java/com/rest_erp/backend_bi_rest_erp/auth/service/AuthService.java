package com.rest_erp.backend_bi_rest_erp.auth.service;


import com.rest_erp.backend_bi_rest_erp.auth.dto.AuthResponse;
import com.rest_erp.backend_bi_rest_erp.auth.entity.AppUserCompany;
import com.rest_erp.backend_bi_rest_erp.auth.entity.UserRegistrationRequest;
import com.rest_erp.backend_bi_rest_erp.auth.entity.UserRegistrationStatus;
import com.rest_erp.backend_bi_rest_erp.auth.repository.AppUserCompanyRepository;
import com.rest_erp.backend_bi_rest_erp.auth.repository.UserRegistrationRequestRepository;
import com.rest_erp.backend_bi_rest_erp.company.dto.CompanyInfo;
import com.rest_erp.backend_bi_rest_erp.company.service.CompanyLookupService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest_erp.backend_bi_rest_erp.auth.entity.AppRole;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final UserRegistrationRequestRepository requestRepository;
    private final CompanyLookupService companyLookupService;
    private final FileStorageService fileStorageService;
    private final AppUserCompanyRepository appUserCompanyRepository;
    private final KeycloakService keycloakService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthService(
            UserRegistrationRequestRepository requestRepository,
            CompanyLookupService companyLookupService,
            FileStorageService fileStorageService, AppUserCompanyRepository appUserCompanyRepository, KeycloakService keycloakService
    ) {
        this.requestRepository = requestRepository;
        this.companyLookupService = companyLookupService;
        this.fileStorageService = fileStorageService;
        this.appUserCompanyRepository = appUserCompanyRepository;
        this.keycloakService = keycloakService;
    }

    public String register(
            String firstName,
            String lastName,
            String email,
            String companyName,
            MultipartFile photo
    ) {
        validateRegisterFields(firstName, lastName, email, companyName);

        boolean hasBlockingRequest = requestRepository.existsByEmailIgnoreCaseAndStatusIn(
                email.trim().toLowerCase(),
                List.of(
                        UserRegistrationStatus.PENDING,
                        UserRegistrationStatus.PENDING_COMPANY_NOT_FOUND,
                        UserRegistrationStatus.APPROVED
                )
        );

        if (hasBlockingRequest) {
            throw new RuntimeException("A registration request already exists or has already been approved with this email");
        }

        String photoUrl = fileStorageService.saveUserPhoto(photo);

        boolean companyExists = companyLookupService.companyExistsByName(companyName);

        CompanyInfo companyInfo = null;

        if (companyExists) {
            companyInfo = companyLookupService.findCompanyByName(companyName);
        }

        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFirstName(firstName.trim());
        request.setLastName(lastName.trim());
        request.setEmail(email.trim().toLowerCase());
        request.setCompanyName(companyName.trim());
        if (companyInfo != null) {
            request.setCompanyId(companyInfo.getCompanyId());
            request.setCompanyKey(companyInfo.getCompanyKey());
        }
        request.setProfileImageUrl(photoUrl);

        if (companyExists) {
            request.setStatus(UserRegistrationStatus.PENDING);
        } else {
            request.setStatus(UserRegistrationStatus.PENDING_COMPANY_NOT_FOUND);
        }

        requestRepository.save(request);

        if (companyExists) {
            return "Registration request sent successfully. Waiting for admin approval.";
        }

        return "Company not found. Your request has been sent to the admin.";
    }

    private void validateRegisterFields(
            String firstName,
            String lastName,
            String email,
            String companyName
    ) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new RuntimeException("First name is required");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            throw new RuntimeException("Last name is required");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        if (companyName == null || companyName.trim().isEmpty()) {
            throw new RuntimeException("Company name is required");
        }
    }

    public AuthResponse login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        AppUserCompany userCompany = appUserCompanyRepository
                .findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new RuntimeException("User is not approved or not linked to a company"));

        if (Boolean.FALSE.equals(userCompany.getActive())) {
            throw new RuntimeException("User account is inactive");
        }

        Map<String, Object> tokenResponse = keycloakService.login(email.trim(), password);

        String accessToken = (String) tokenResponse.get("access_token");

        AppRole keycloakRole = extractRoleFromAccessToken(accessToken);

        if (keycloakRole != null && userCompany.getRole() != keycloakRole) {
            userCompany.setRole(keycloakRole);
            userCompany = appUserCompanyRepository.save(userCompany);
        }

        AuthResponse response = new AuthResponse();

        response.setAccessToken(accessToken);
        response.setRefreshToken((String) tokenResponse.get("refresh_token"));
        response.setTokenType((String) tokenResponse.get("token_type"));

        Object expiresIn = tokenResponse.get("expires_in");
        if (expiresIn instanceof Number) {
            response.setExpiresIn(((Number) expiresIn).longValue());
        }

        response.setEmail(userCompany.getEmail());
        response.setFirstName(userCompany.getFirstName());
        response.setLastName(userCompany.getLastName());
        response.setCompanyId(userCompany.getCompanyId());
        response.setCompanyKey(userCompany.getCompanyKey());
        response.setCompanyName(userCompany.getCompanyName());
        response.setRole(userCompany.getRole().name());
        response.setProfileImageUrl(userCompany.getProfileImageUrl());

        return response;
    }

    public String forgotPassword(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        String normalizedEmail = email.trim().toLowerCase();

        AppUserCompany userCompany = appUserCompanyRepository
                .findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("No approved account found with this email"));

        if (Boolean.FALSE.equals(userCompany.getActive())) {
            throw new RuntimeException("User account is inactive");
        }

        String keycloakUserId = userCompany.getKeycloakUserId();

        if (keycloakUserId == null || keycloakUserId.isBlank()) {
            keycloakUserId = keycloakService.findUserIdByEmail(normalizedEmail);
        }

        if (keycloakUserId == null) {
            throw new RuntimeException("User was not found in Keycloak");
        }

        keycloakService.sendUpdatePasswordEmail(keycloakUserId);

        return "Password reset email sent successfully.";
    }

    private AppRole extractRoleFromAccessToken(String accessToken) {
        try {
            if (accessToken == null || accessToken.isBlank()) {
                return null;
            }

            String[] parts = accessToken.split("\\.");

            if (parts.length < 2) {
                return null;
            }

            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );

            JsonNode payload = objectMapper.readTree(payloadJson);
            JsonNode rolesNode = payload.path("realm_access").path("roles");

            if (!rolesNode.isArray()) {
                return null;
            }

            boolean hasSuperAdmin = false;
            boolean hasCompanyAdmin = false;
            boolean hasUser = false;

            for (JsonNode roleNode : rolesNode) {
                String role = roleNode.asText();

                if ("SUPER_ADMIN".equals(role)) {
                    hasSuperAdmin = true;
                }

                if ("COMPANY_ADMIN".equals(role)) {
                    hasCompanyAdmin = true;
                }

                if ("USER".equals(role)) {
                    hasUser = true;
                }
            }

            if (hasSuperAdmin) {
                return AppRole.SUPER_ADMIN;
            }

            if (hasCompanyAdmin) {
                return AppRole.COMPANY_ADMIN;
            }

            if (hasUser) {
                return AppRole.USER;
            }

            return null;

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract role from Keycloak token", e);
        }
    }
}
