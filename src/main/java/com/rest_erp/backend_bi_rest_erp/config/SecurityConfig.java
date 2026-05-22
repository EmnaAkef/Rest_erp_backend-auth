package com.rest_erp.backend_bi_rest_erp.config;

import com.rest_erp.backend_bi_rest_erp.auth.filter.JwtCompanyFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig {

    private final JwtCompanyFilter jwtCompanyFilter;

    public SecurityConfig(JwtCompanyFilter jwtCompanyFilter) {
        this.jwtCompanyFilter = jwtCompanyFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/uploads/users/**").permitAll()
                        .requestMatchers("/api/admin/registration-requests/**").permitAll()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtCompanyFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}