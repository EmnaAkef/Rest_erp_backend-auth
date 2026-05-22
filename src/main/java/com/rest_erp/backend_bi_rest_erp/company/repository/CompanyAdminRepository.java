package com.rest_erp.backend_bi_rest_erp.company.repository;

import com.rest_erp.backend_bi_rest_erp.company.dto.CompanyOptionResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CompanyAdminRepository {

    private final JdbcTemplate jdbcTemplate;

    public CompanyAdminRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CompanyOptionResponse> getCompanyOptions() {
        String sql = """
            SELECT
                company_id,
                company_key,
                company_name
            FROM dim_company
            WHERE is_current = true
              AND active = true
            ORDER BY company_name
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new CompanyOptionResponse(
                        rs.getLong("company_id"),
                        rs.getInt("company_key"),
                        rs.getString("company_name")
                )
        );
    }
}