package com.rest_erp.backend_bi_rest_erp.auth.controller;

import com.rest_erp.backend_bi_rest_erp.bi.tenant.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthTestController {

    @GetMapping("/company-key")
    public ResponseEntity<Integer> getCompanyKey() {
        Integer companyKey = TenantContext.getCompanyKey();

        if (companyKey == null) {
            throw new RuntimeException("No company key found from token");
        }

        return ResponseEntity.ok(companyKey);
    }

    @GetMapping("/current-user-context")
    public ResponseEntity<Map<String, Object>> getCurrentUserContext() {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("email", TenantContext.getUserEmail());
        response.put("role", TenantContext.getUserRole() != null ? TenantContext.getUserRole().name() : null);
        response.put("companyId", TenantContext.getCompanyId());
        response.put("companyKey", TenantContext.getCompanyKey());
        response.put("isSuperAdmin", TenantContext.isSuperAdmin());
        response.put("isCompanyAdmin", TenantContext.isCompanyAdmin());
        response.put("isUser", TenantContext.isUser());

        return ResponseEntity.ok(response);
    }
}