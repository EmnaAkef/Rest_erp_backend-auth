package com.rest_erp.backend_bi_rest_erp.company.service;

import com.rest_erp.backend_bi_rest_erp.company.dto.CompanyInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CompanyLookupService {

    private final JdbcTemplate jdbcTemplate;

    public CompanyLookupService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean companyExistsByName(String companyName) {
        String sql = """
            SELECT COUNT(*)
            FROM public.dim_company
            WHERE LOWER(company_name) = LOWER(?)
              AND is_current = true
        """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, companyName.trim());

        return count != null && count > 0;
    }

    public CompanyInfo findCompanyByName(String companyName) {
        String sql = """
            SELECT company_id, company_key, company_name
            FROM public.dim_company
            WHERE LOWER(company_name) = LOWER(?)
              AND is_current = true
            LIMIT 1
        """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> new CompanyInfo(
                        rs.getLong("company_id"),
                        rs.getInt("company_key"),
                        rs.getString("company_name")
                ),
                companyName.trim()
        );
    }
}