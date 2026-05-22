package com.rest_erp.backend_bi_rest_erp.company.service;

import com.rest_erp.backend_bi_rest_erp.auth.entity.AppRole;
import com.rest_erp.backend_bi_rest_erp.bi.tenant.TenantContext;
import com.rest_erp.backend_bi_rest_erp.company.dto.CompanyOptionResponse;
import com.rest_erp.backend_bi_rest_erp.company.repository.CompanyAdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyAdminService {

    private final CompanyAdminRepository companyAdminRepository;

    public CompanyAdminService(CompanyAdminRepository companyAdminRepository) {
        this.companyAdminRepository = companyAdminRepository;
    }

    public List<CompanyOptionResponse> getCompanyOptionsForCurrentUser() {
        AppRole role = TenantContext.getUserRole();

        if (role != AppRole.SUPER_ADMIN) {
            throw new RuntimeException("Access denied: only super admin can list all companies");
        }

        return companyAdminRepository.getCompanyOptions();
    }
}