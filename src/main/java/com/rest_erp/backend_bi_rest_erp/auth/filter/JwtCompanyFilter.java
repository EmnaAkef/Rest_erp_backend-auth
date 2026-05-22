package com.rest_erp.backend_bi_rest_erp.auth.filter;

import com.rest_erp.backend_bi_rest_erp.auth.entity.AppRole;
import com.rest_erp.backend_bi_rest_erp.auth.entity.AppUserCompany;
import com.rest_erp.backend_bi_rest_erp.auth.repository.AppUserCompanyRepository;
import com.rest_erp.backend_bi_rest_erp.bi.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Component
public class JwtCompanyFilter extends OncePerRequestFilter {

    private final AppUserCompanyRepository appUserCompanyRepository;

    public JwtCompanyFilter(AppUserCompanyRepository appUserCompanyRepository) {
        this.appUserCompanyRepository = appUserCompanyRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                String email = extractEmailFromToken(token);

                if (email != null) {
                    Optional<AppUserCompany> userCompany =
                            appUserCompanyRepository.findByEmailIgnoreCase(email);

                    if (userCompany.isPresent() && Boolean.TRUE.equals(userCompany.get().getActive())) {
                        AppUserCompany currentUser = userCompany.get();

                        TenantContext.setUserEmail(currentUser.getEmail());
                        TenantContext.setUserRole(currentUser.getRole());

                        if (currentUser.getRole() == AppRole.SUPER_ADMIN) {
                            String selectedCompanyKeyHeader = request.getHeader("X-Company-Key");

                            if (selectedCompanyKeyHeader != null && !selectedCompanyKeyHeader.isBlank()) {
                                try {
                                    Integer selectedCompanyKey = Integer.valueOf(selectedCompanyKeyHeader);
                                    TenantContext.setCompanyKey(selectedCompanyKey);
                                } catch (NumberFormatException e) {
                                    throw new RuntimeException("Invalid X-Company-Key header");
                                }
                            }
                        } else {
                            TenantContext.setCompanyId(currentUser.getCompanyId());
                            TenantContext.setCompanyKey(currentUser.getCompanyKey());
                        }
                    }
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
    }

    private String extractEmailFromToken(String token) {
        try {
            String[] chunks = token.split("\\.");

            if (chunks.length < 2) {
                return null;
            }

            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]), StandardCharsets.UTF_8);

            String emailKey = "\"email\":\"";
            int start = payload.indexOf(emailKey);

            if (start == -1) {
                return null;
            }

            start += emailKey.length();
            int end = payload.indexOf("\"", start);

            if (end == -1) {
                return null;
            }

            return payload.substring(start, end);

        } catch (Exception e) {
            return null;
        }
    }
}