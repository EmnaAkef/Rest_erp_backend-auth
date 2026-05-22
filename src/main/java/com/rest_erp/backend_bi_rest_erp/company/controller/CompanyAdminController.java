package com.rest_erp.backend_bi_rest_erp.company.controller;

import com.rest_erp.backend_bi_rest_erp.company.dto.CompanyOptionResponse;
import com.rest_erp.backend_bi_rest_erp.company.service.CompanyAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/companies")
@CrossOrigin(origins = "http://localhost:4200")
public class CompanyAdminController {

    private final CompanyAdminService companyAdminService;

    public CompanyAdminController(CompanyAdminService companyAdminService) {
        this.companyAdminService = companyAdminService;
    }

    @GetMapping
    public ResponseEntity<List<CompanyOptionResponse>> getCompanies() {
        return ResponseEntity.ok(companyAdminService.getCompanyOptionsForCurrentUser());
    }
}