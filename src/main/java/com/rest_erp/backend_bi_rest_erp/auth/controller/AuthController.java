package com.rest_erp.backend_bi_rest_erp.auth.controller;


import com.rest_erp.backend_bi_rest_erp.auth.service.AuthService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.rest_erp.backend_bi_rest_erp.auth.dto.AuthResponse;
import com.rest_erp.backend_bi_rest_erp.auth.dto.LoginRequest;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> register(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("companyName") String companyName,
            @RequestParam(value = "photo", required = false) MultipartFile photo
    ) {
        String message = authService.register(
                firstName,
                lastName,
                email,
                companyName,
                photo
        );

        return ResponseEntity.ok(message);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        return ResponseEntity.ok(response);
    }
}
