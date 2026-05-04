package com.rest_erp.backend_bi_rest_erp.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class CommonRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public String getCompanyCurrency(Integer companyKey) {
        String sql = """
            SELECT COALESCE(c.currency, 'DT')
            FROM dim_company c
            WHERE c.company_key = :companyKey
              AND c.is_current = true
            LIMIT 1
        """;

        Object result = entityManager.createNativeQuery(sql)
                .setParameter("companyKey", companyKey)
                .getSingleResult();

        return result != null ? result.toString() : "DT";
    }


}
