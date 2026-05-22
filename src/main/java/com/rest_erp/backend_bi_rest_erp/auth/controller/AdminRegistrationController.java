package com.rest_erp.backend_bi_rest_erp.auth.controller;

import com.rest_erp.backend_bi_rest_erp.auth.dto.RejectRequest;
import com.rest_erp.backend_bi_rest_erp.auth.entity.UserRegistrationRequest;
import com.rest_erp.backend_bi_rest_erp.auth.service.AdminRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/registration-requests")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminRegistrationController {

    private final AdminRegistrationService adminRegistrationService;

    public AdminRegistrationController(AdminRegistrationService adminRegistrationService) {
        this.adminRegistrationService = adminRegistrationService;
    }

    @GetMapping
    public ResponseEntity<List<UserRegistrationRequest>> getAllRequests() {
        return ResponseEntity.ok(adminRegistrationService.getAllRequests());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<UserRegistrationRequest>> getPendingRequests() {
        return ResponseEntity.ok(adminRegistrationService.getPendingRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRegistrationRequest> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(adminRegistrationService.getRequestById(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approveRequest(@PathVariable Long id) {
        String message = adminRegistrationService.approveRequest(id);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectRequest(
            @PathVariable Long id,
            @RequestBody(required = false) RejectRequest rejectRequest
    ) {
        String reason = rejectRequest != null ? rejectRequest.getReason() : null;
        String message = adminRegistrationService.rejectRequest(id, reason);
        return ResponseEntity.ok(message);
    }
}