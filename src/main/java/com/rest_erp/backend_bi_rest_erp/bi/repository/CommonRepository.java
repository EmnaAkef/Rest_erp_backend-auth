package com.rest_erp.backend_bi_rest_erp.bi.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CommonRepository {

    private final JdbcTemplate jdbcTemplate;

    public CommonRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getCompanyCurrency(Integer companyKey) {
        if (companyKey == null) {
            return "DT";
        }

        String sql = """
            SELECT COALESCE(c.currency, 'DT')
            FROM dim_company c
            WHERE c.company_key = ?
              AND c.is_current = true
            LIMIT 1
        """;

        try {
            String currency = jdbcTemplate.queryForObject(sql, String.class, companyKey);
            return currency != null ? currency : "DT";
        } catch (EmptyResultDataAccessException e) {
            return "DT";
        }
    }
}