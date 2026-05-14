package com.rest_erp.backend_bi_rest_erp.repository.sales;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import com.rest_erp.backend_bi_rest_erp.dto.sales.SalesFilterRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public class SalesKpiRepository {

    @PersistenceContext
    private EntityManager entityManager;



    public BigDecimal getTotalRevenue(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(f.allocated_amount), 0) + COALESCE(SUM(f.invoice_total), 0)
        FROM fact_sales_financials f
        JOIN dim_date d ON d.date_key = f.date_key
        LEFT JOIN dim_customer c ON c.customer_key = f.customer_key
        LEFT JOIN dim_user u ON u.user_key = f.agent_user_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getCustomerId() != null) {
            sql.append(" AND c.customer_id = :customerId");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
            AND COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) = :customerName
        """);
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            sql.append(" AND f.agent_user_key = :salespersonKey");
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getCustomerId() != null) {
            query.setParameter("customerId", filters.getCustomerId());
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            query.setParameter("salespersonKey", filters.getSalespersonKey());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        Object result = query.getSingleResult();

        return result != null ? new BigDecimal(result.toString()) : BigDecimal.ZERO;
    }

    public Long getNumberOfDeals(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(DISTINCT f.deal_id)
        FROM fact_deal f
        JOIN dim_date d ON d.date_key = f.close_date_key
        LEFT JOIN dim_customer c ON c.customer_key = f.customer_key
        LEFT JOIN dim_user u ON u.user_key = f.owner_user_key
        LEFT JOIN dim_workstatus ws ON ws.workstatus_key = f.workstatus_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getCustomerId() != null) {
            sql.append(" AND c.customer_id = :customerId");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
            AND COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) = :customerName
        """);
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            sql.append(" AND f.owner_user_key = :salespersonKey");
        }

        if (filters != null && filters.getWorkstatusLabel() != null && !filters.getWorkstatusLabel().isBlank()) {
            sql.append(" AND ws.status_label = :workstatusLabel");
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getCustomerId() != null) {
            query.setParameter("customerId", filters.getCustomerId());
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            query.setParameter("salespersonKey", filters.getSalespersonKey());
        }

        if (filters != null && filters.getWorkstatusLabel() != null && !filters.getWorkstatusLabel().isBlank()) {
            query.setParameter("workstatusLabel", filters.getWorkstatusLabel());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        Object result = query.getSingleResult();

        return result != null ? ((Number) result).longValue() : 0L;
    }

    public BigDecimal getWinRate(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT 
            CASE 
                WHEN COUNT(CASE WHEN f.close_date_key IS NOT NULL THEN 1 END) = 0 THEN 0
                ELSE (
                    COUNT(CASE 
                        WHEN f.close_date_key IS NOT NULL
                         AND ws.status_label IN ('Win', 'Done')
                        THEN 1
                    END) * 100.0
                ) / COUNT(CASE WHEN f.close_date_key IS NOT NULL THEN 1 END)
            END
        FROM fact_deal f
        LEFT JOIN dim_customer c ON c.customer_key = f.customer_key
        LEFT JOIN dim_user u ON u.user_key = f.owner_user_key
        LEFT JOIN dim_workstatus ws ON ws.workstatus_key = f.workstatus_key
        LEFT JOIN dim_date d ON d.date_key = f.close_date_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getCustomerId() != null) {
            sql.append(" AND c.customer_id = :customerId");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
            AND COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) = :customerName
        """);
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            sql.append(" AND f.owner_user_key = :salespersonKey");
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getCustomerId() != null) {
            query.setParameter("customerId", filters.getCustomerId());
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            query.setParameter("salespersonKey", filters.getSalespersonKey());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        Object result = query.getSingleResult();

        if (result == null) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(result.toString());
    }

    public BigDecimal getAverageDealValue(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(AVG(f.deal_value), 0)
        FROM fact_deal f
        LEFT JOIN dim_customer c ON c.customer_key = f.customer_key
        LEFT JOIN dim_user u ON u.user_key = f.owner_user_key
        LEFT JOIN dim_date d ON d.date_key = f.close_date_key
        LEFT JOIN dim_workstatus ws ON ws.workstatus_key = f.workstatus_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getCustomerId() != null) {
            sql.append(" AND c.customer_id = :customerId");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
            AND COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) = :customerName
        """);
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            sql.append(" AND f.owner_user_key = :salespersonKey");
        }

        if (filters != null && filters.getWorkstatusLabel() != null && !filters.getWorkstatusLabel().isBlank()) {
            sql.append(" AND ws.status_label = :workstatusLabel");
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getCustomerId() != null) {
            query.setParameter("customerId", filters.getCustomerId());
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            query.setParameter("salespersonKey", filters.getSalespersonKey());
        }

        if (filters != null && filters.getWorkstatusLabel() != null && !filters.getWorkstatusLabel().isBlank()) {
            query.setParameter("workstatusLabel", filters.getWorkstatusLabel());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        Object result = query.getSingleResult();

        return result != null ? new BigDecimal(result.toString()) : BigDecimal.ZERO;
    }

    public Long getSalesOrdersCount(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(DISTINCT f.sell_order_id)
        FROM fact_sales_order f
        LEFT JOIN dim_date d ON d.date_key = f.date_key
        LEFT JOIN dim_customer c ON c.customer_key = f.customer_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getCustomerId() != null) {
            sql.append(" AND c.customer_id = :customerId");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
            AND COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) = :customerName
        """);
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getCustomerId() != null) {
            query.setParameter("customerId", filters.getCustomerId());
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        Object result = query.getSingleResult();

        return result != null ? ((Number) result).longValue() : 0L;
    }

    public BigDecimal getOutstandingReceivables(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(f.total), 0)
        FROM fact_invoice f
        JOIN dim_invoice_status s ON s.status_key = f.status_key
        LEFT JOIN dim_date d ON d.date_key = f.date_key
        LEFT JOIN dim_customer c ON c.customer_key = f.customer_key
        WHERE f.company_key = :companyKey
          AND s.status_group = 'PENDING'
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getCustomerId() != null) {
            sql.append(" AND c.customer_id = :customerId");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
            AND COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) = :customerName
        """);
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getCustomerId() != null) {
            query.setParameter("customerId", filters.getCustomerId());
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        Object result = query.getSingleResult();

        return result != null ? new BigDecimal(result.toString()) : BigDecimal.ZERO;
    }

    public Long getPipelineDealsCount(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(DISTINCT f.deal_id)
        FROM fact_deal f
        JOIN dim_workstatus w ON w.workstatus_key = f.workstatus_key
        LEFT JOIN dim_date d ON d.date_key = f.close_date_key
        LEFT JOIN dim_customer c ON c.customer_key = f.customer_key
        LEFT JOIN dim_user u ON u.user_key = f.owner_user_key
        WHERE f.company_key = :companyKey
          AND COALESCE(f.is_archived, false) = false
          AND w.status_label IN ('Generated', 'Initial Contact', 'Backlog', 'To Do', 'In Progress', 'In Review')
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
            AND COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) = :customerName
        """);
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            sql.append(" AND f.owner_user_key = :salespersonKey");
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            query.setParameter("salespersonKey", filters.getSalespersonKey());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        Object result = query.getSingleResult();

        return result != null ? ((Number) result).longValue() : 0L;
    }

    public BigDecimal getPipelineValue(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(f.deal_value), 0)
        FROM fact_deal f
        JOIN dim_workstatus w ON w.workstatus_key = f.workstatus_key
        LEFT JOIN dim_date d ON d.date_key = f.close_date_key
        LEFT JOIN dim_customer c ON c.customer_key = f.customer_key
        LEFT JOIN dim_user u ON u.user_key = f.owner_user_key
        WHERE f.company_key = :companyKey
          AND COALESCE(f.is_archived, false) = false
          AND w.status_label IN ('Generated', 'Initial Contact', 'Backlog', 'To Do', 'In Progress', 'In Review')
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
            AND COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) = :customerName
        """);
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            sql.append(" AND f.owner_user_key = :salespersonKey");
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            query.setParameter("salespersonKey", filters.getSalespersonKey());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        Object result = query.getSingleResult();

        return result != null ? new BigDecimal(result.toString()) : BigDecimal.ZERO;
    }

    public Long getActiveCustomers(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(DISTINCT COALESCE(
            NULLIF(TRIM(c.organization_name), ''),
            NULLIF(TRIM(c.contact_name), ''),
            'Unknown Customer'
        ))
        FROM fact_invoice f
        JOIN dim_invoice_status s ON s.status_key = f.status_key
        JOIN dim_date d ON d.date_key = f.date_key
        LEFT JOIN dim_customer c ON c.customer_key = f.customer_key
        WHERE f.company_key = :companyKey
          AND s.status_group = 'VALIDATED'
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getCustomerId() != null) {
            sql.append(" AND c.customer_id = :customerId");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
            AND COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) = :customerName
        """);
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getCustomerId() != null) {
            query.setParameter("customerId", filters.getCustomerId());
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        Object result = query.getSingleResult();

        return result != null ? ((Number) result).longValue() : 0L;
    }

    public Long getTotalCustomers(
            Integer companyKey,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(DISTINCT COALESCE(
            NULLIF(TRIM(c.organization_name), ''),
            NULLIF(TRIM(c.contact_name), ''),
            'Unknown Customer'
        ))
        FROM dim_customer c
        WHERE c.company_key = :companyKey
    """);

        if (filters != null && filters.getCustomerId() != null) {
            sql.append(" AND c.customer_id = :customerId");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
            AND COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) = :customerName
        """);
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (filters != null && filters.getCustomerId() != null) {
            query.setParameter("customerId", filters.getCustomerId());
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        Object result = query.getSingleResult();

        return result != null ? ((Number) result).longValue() : 0L;
    }

    public BigDecimal getConversionRate(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT
            CASE
                WHEN COUNT(DISTINCT f.sell_order_id) = 0 THEN 0
                ELSE (
                    COUNT(DISTINCT CASE WHEN f.quotation_id IS NOT NULL THEN f.sell_order_id END) * 100.0
                ) / COUNT(DISTINCT f.sell_order_id)
            END
        FROM fact_sales_order f
        LEFT JOIN dim_date d ON d.date_key = f.date_key
        LEFT JOIN dim_customer c ON c.customer_key = f.customer_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getCustomerId() != null) {
            sql.append(" AND c.customer_id = :customerId");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
            AND COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) = :customerName
        """);
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getCustomerId() != null) {
            query.setParameter("customerId", filters.getCustomerId());
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        Object result = query.getSingleResult();

        return result != null ? new BigDecimal(result.toString()) : BigDecimal.ZERO;
    }

    public List<Object[]> getRevenueTrend(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT 
            TO_CHAR(DATE_TRUNC('month', d.full_date), 'Mon YYYY') AS label,
            COALESCE(SUM(f.allocated_amount), 0) + COALESCE(SUM(f.invoice_total), 0) AS value
        FROM fact_sales_financials f
        JOIN dim_date d ON d.date_key = f.date_key
        LEFT JOIN dim_customer c ON c.customer_key = f.customer_key
        LEFT JOIN dim_user u ON u.user_key = f.agent_user_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
            AND COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) = :customerName
        """);
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            sql.append(" AND f.agent_user_key = :salespersonKey");
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        sql.append("""
        GROUP BY DATE_TRUNC('month', d.full_date)
        ORDER BY DATE_TRUNC('month', d.full_date)
    """);

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            query.setParameter("salespersonKey", filters.getSalespersonKey());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        return query.getResultList();
    }

    public List<Object[]> getPipelineDistribution(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT 
            w.status_label,
            COUNT(DISTINCT f.deal_id) AS total_deals
        FROM fact_deal f
        JOIN dim_workstatus w ON w.workstatus_key = f.workstatus_key
        JOIN dim_date d ON d.date_key = f.close_date_key
        LEFT JOIN dim_customer c ON c.customer_key = f.customer_key
        LEFT JOIN dim_user u ON u.user_key = f.owner_user_key
        WHERE f.company_key = :companyKey
          AND COALESCE(f.is_archived, false) = false
          AND w.status_label IN (
              'Generated',
              'Initial Contact',
              'Backlog',
              'To Do',
              'In Progress',
              'In Review',
              'Done',
              'Win',
              'Lost',
              'Blocked'
          )
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
            AND COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) = :customerName
        """);
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            sql.append(" AND f.owner_user_key = :salespersonKey");
        }

        if (filters != null && filters.getWorkstatusLabel() != null && !filters.getWorkstatusLabel().isBlank()) {
            sql.append(" AND w.status_label = :workstatusLabel");
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        sql.append("""
        GROUP BY w.status_label
        ORDER BY CASE w.status_label
            WHEN 'Generated' THEN 1
            WHEN 'Initial Contact' THEN 2
            WHEN 'Backlog' THEN 3
            WHEN 'To Do' THEN 4
            WHEN 'In Progress' THEN 5
            WHEN 'In Review' THEN 6
            WHEN 'Blocked' THEN 7
            WHEN 'Done' THEN 8
            WHEN 'Win' THEN 9
            WHEN 'Lost' THEN 10
            ELSE 11
        END
    """);

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            query.setParameter("salespersonKey", filters.getSalespersonKey());
        }

        if (filters != null && filters.getWorkstatusLabel() != null && !filters.getWorkstatusLabel().isBlank()) {
            query.setParameter("workstatusLabel", filters.getWorkstatusLabel());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        return query.getResultList();
    }

    public List<Object[]> getRecentSalesOrders(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT 
            f.sell_order_id,
            COALESCE(c.organization_name, c.contact_name, 'Unknown Customer') AS customer,
            d.full_date,
            COALESCE(SUM(sl.line_revenue), 0) AS amount,
            f.status
        FROM fact_sales_order f
        LEFT JOIN dim_customer c ON c.customer_key = f.customer_key
        LEFT JOIN dim_date d ON d.date_key = f.date_key
        LEFT JOIN fact_sales_line sl 
            ON sl.sell_order_id = f.sell_order_id
           AND sl.company_key = f.company_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
            AND COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) = :customerName
        """);
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        sql.append("""
        GROUP BY 
            f.sell_order_id,
            c.organization_name,
            c.contact_name,
            d.full_date,
            f.status
        ORDER BY d.full_date DESC
        LIMIT 10
    """);

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        return query.getResultList();
    }

    public List<Object[]> getTopSalespersons(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT 
            COALESCE(u.user_name, 'Unknown Salesperson') AS name,
            COALESCE(SUM(f.allocated_amount), 0) + COALESCE(SUM(f.invoice_total), 0) AS amount
        FROM fact_sales_financials f
        LEFT JOIN dim_user u ON u.user_key = f.agent_user_key
        LEFT JOIN dim_customer c ON c.customer_key = f.customer_key
        LEFT JOIN dim_date d ON d.date_key = f.date_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
            AND COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) = :customerName
        """);
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            sql.append(" AND f.agent_user_key = :salespersonKey");
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        sql.append("""
        GROUP BY u.user_name
        ORDER BY amount DESC
        LIMIT 5
    """);

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            query.setParameter("salespersonKey", filters.getSalespersonKey());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        return query.getResultList();
    }

    public List<Object[]> getRevenueByCustomer(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT
            COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) AS customer_name,
            COALESCE(SUM(f.allocated_amount), 0) + COALESCE(SUM(f.invoice_total), 0) AS amount
        FROM fact_sales_financials f
        LEFT JOIN dim_customer c ON c.customer_key = f.customer_key
        LEFT JOIN dim_user u ON u.user_key = f.agent_user_key
        LEFT JOIN dim_date d ON d.date_key = f.date_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
            AND COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) = :customerName
        """);
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            sql.append(" AND f.agent_user_key = :salespersonKey");
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        sql.append("""
        GROUP BY customer_name
        ORDER BY amount DESC
        LIMIT 10
    """);

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            query.setParameter("salespersonKey", filters.getSalespersonKey());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        return query.getResultList();
    }

    public List<Object[]> getRevenueByProduct(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT 
            COALESCE(p.product_name, 'Unknown Product') AS product_name,
            COALESCE(SUM(sl.line_revenue), 0) AS amount
        FROM fact_sales_line sl
        LEFT JOIN dim_product p ON p.product_key = sl.product_key
        LEFT JOIN dim_date d ON d.date_key = sl.date_key
        WHERE sl.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getProductKey() != null) {
            sql.append(" AND sl.product_key = :productKey");
        }

        if (filters != null && filters.getProductCategory() != null && !filters.getProductCategory().isBlank()) {
            sql.append(" AND p.category = :productCategory");
        }

        sql.append("""
        GROUP BY p.product_name
        ORDER BY amount DESC
        LIMIT 10
    """);

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getProductKey() != null) {
            query.setParameter("productKey", filters.getProductKey());
        }

        if (filters != null && filters.getProductCategory() != null && !filters.getProductCategory().isBlank()) {
            query.setParameter("productCategory", filters.getProductCategory());
        }

        return query.getResultList();
    }

    public List<Object[]> getCustomerRetention(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        WITH monthly_active AS (
            SELECT 
                d.year,
                d.month,
                DATE_TRUNC('month', d.full_date)::date AS month_date,
                COALESCE(
                    NULLIF(TRIM(c.organization_name), ''),
                    NULLIF(TRIM(c.contact_name), ''),
                    'Unknown Customer'
                ) AS customer_name
            FROM fact_sales_financials f
            JOIN dim_date d ON d.date_key = f.date_key
            LEFT JOIN dim_customer c ON c.customer_key = f.customer_key
            WHERE f.company_key = :companyKey
              AND f.customer_key IS NOT NULL
    """);

        if (startDate != null) {
            sql.append("""
              AND d.full_date >= (date_trunc('month', CAST(:startDate AS date)) - INTERVAL '1 month')::date
        """);
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
              AND COALESCE(
                  NULLIF(TRIM(c.organization_name), ''),
                  NULLIF(TRIM(c.contact_name), ''),
                  'Unknown Customer'
              ) = :customerName
        """);
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        sql.append("""
            GROUP BY d.year, d.month, DATE_TRUNC('month', d.full_date),
                     COALESCE(
                         NULLIF(TRIM(c.organization_name), ''),
                         NULLIF(TRIM(c.contact_name), ''),
                         'Unknown Customer'
                     )
        ),

        monthly_counts AS (
            SELECT
                month_date,
                COUNT(DISTINCT customer_name) AS total_customers
            FROM monthly_active
            GROUP BY month_date
        ),

        retained_customers AS (
            SELECT
                curr.month_date,
                COUNT(DISTINCT curr.customer_name) AS retained_customers
            FROM monthly_active curr
            JOIN monthly_active prev
                ON curr.customer_name = prev.customer_name
               AND curr.month_date = prev.month_date + INTERVAL '1 month'
            GROUP BY curr.month_date
        )

        SELECT
            TO_CHAR(curr.month_date, 'YYYY-MM') AS label,
            CASE
                WHEN COALESCE(prev.total_customers, 0) = 0 THEN 0
                ELSE ROUND(
                    COALESCE(ret.retained_customers, 0)::numeric 
                    * 100.0 
                    / prev.total_customers,
                    2
                )
            END AS retention_rate
        FROM monthly_counts curr
        LEFT JOIN monthly_counts prev
            ON curr.month_date = prev.month_date + INTERVAL '1 month'
        LEFT JOIN retained_customers ret
            ON curr.month_date = ret.month_date
        WHERE 1 = 1
    """);

        if (startDate != null) {
            sql.append("""
            AND curr.month_date >= date_trunc('month', CAST(:startDate AS date))::date
        """);
        }

        if (endDate != null) {
            sql.append("""
            AND curr.month_date <= date_trunc('month', CAST(:endDate AS date))::date
        """);
        }

        sql.append(" ORDER BY curr.month_date");

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        return query.getResultList();
    }

    public List<Object[]> getHighValueDeals(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {

        StringBuilder sql = new StringBuilder("""
        SELECT
            f.deal_id,
            f.deal_value
        FROM fact_deal f
        LEFT JOIN dim_date d ON d.date_key = f.close_date_key
        LEFT JOIN dim_customer c ON c.customer_key = f.customer_key
        LEFT JOIN dim_user u ON u.user_key = f.owner_user_key
        LEFT JOIN dim_workstatus ws ON ws.workstatus_key = f.workstatus_key
        WHERE f.company_key = :companyKey
          AND f.deal_value IS NOT NULL
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        if (filters != null && filters.getCustomerId() != null) {
            sql.append(" AND c.customer_id = :customerId");
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            sql.append("""
            AND COALESCE(
                NULLIF(TRIM(c.organization_name), ''),
                NULLIF(TRIM(c.contact_name), ''),
                'Unknown Customer'
            ) = :customerName
        """);
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            sql.append(" AND f.owner_user_key = :salespersonKey");
        }

        if (filters != null && filters.getWorkstatusLabel() != null && !filters.getWorkstatusLabel().isBlank()) {
            sql.append(" AND ws.status_label = :workstatusLabel");
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            sql.append(" AND c.client_category = :customerCategory");
        }

        sql.append("""
        ORDER BY f.deal_value DESC
        LIMIT 5
    """);

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (filters != null && filters.getCustomerId() != null) {
            query.setParameter("customerId", filters.getCustomerId());
        }

        if (filters != null && filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
            query.setParameter("customerName", filters.getCustomerName());
        }

        if (filters != null && filters.getSalespersonKey() != null) {
            query.setParameter("salespersonKey", filters.getSalespersonKey());
        }

        if (filters != null && filters.getWorkstatusLabel() != null && !filters.getWorkstatusLabel().isBlank()) {
            query.setParameter("workstatusLabel", filters.getWorkstatusLabel());
        }

        if (filters != null && filters.getCustomerCategory() != null && !filters.getCustomerCategory().isBlank()) {
            query.setParameter("customerCategory", filters.getCustomerCategory());
        }

        return query.getResultList();
    }

    public List<Object[]> getCustomerOptions(Integer companyKey) {
        String sql = """
        SELECT
            customer_name AS value,
            customer_name AS label
        FROM (
            SELECT DISTINCT
                COALESCE(
                    NULLIF(TRIM(c.organization_name), ''),
                    NULLIF(TRIM(c.contact_name), ''),
                    'Unknown Customer'
                ) AS customer_name
            FROM dim_customer c
            WHERE c.company_key = :companyKey
        ) x
        WHERE customer_name IS NOT NULL
        ORDER BY customer_name
    """;

        return entityManager.createNativeQuery(sql)
                .setParameter("companyKey", companyKey)
                .getResultList();
    }

    public List<Object[]> getProductOptions(Integer companyKey) {
        String sql = """
        SELECT DISTINCT
            p.product_key,
            COALESCE(p.product_name, 'Unknown Product') AS product_name
        FROM dim_product p
        JOIN fact_sales_line sl ON sl.product_key = p.product_key
        WHERE sl.company_key = :companyKey
        ORDER BY product_name
    """;

        return entityManager.createNativeQuery(sql)
                .setParameter("companyKey", companyKey)
                .getResultList();
    }

    public List<Object[]> getSalespersonOptions(Integer companyKey) {
        String sql = """
        SELECT DISTINCT
            u.user_key,
            COALESCE(u.user_name, 'Unknown Salesperson') AS user_name
        FROM dim_user u
        WHERE u.company_key = :companyKey
          AND u.user_key IN (
              SELECT DISTINCT agent_user_key
              FROM fact_sales_financials
              WHERE company_key = :companyKey
                AND agent_user_key IS NOT NULL

              UNION

              SELECT DISTINCT owner_user_key
              FROM fact_deal
              WHERE company_key = :companyKey
                AND owner_user_key IS NOT NULL
          )
        ORDER BY user_name
    """;

        return entityManager.createNativeQuery(sql)
                .setParameter("companyKey", companyKey)
                .getResultList();
    }

    public List<String> getWorkstatusOptions(Integer companyKey) {
        String sql = """
        SELECT DISTINCT
            ws.status_label
        FROM dim_workstatus ws
        JOIN fact_deal f ON f.workstatus_key = ws.workstatus_key
        WHERE f.company_key = :companyKey
          AND ws.status_label IS NOT NULL
        ORDER BY ws.status_label
    """;

        return entityManager.createNativeQuery(sql)
                .setParameter("companyKey", companyKey)
                .getResultList();
    }

    public List<String> getCustomerCategoryOptions(Integer companyKey) {
        String sql = """
        SELECT DISTINCT
            c.client_category
        FROM dim_customer c
        WHERE c.company_key = :companyKey
          AND c.client_category IS NOT NULL
          AND TRIM(c.client_category) <> ''
        ORDER BY c.client_category
    """;

        return entityManager.createNativeQuery(sql)
                .setParameter("companyKey", companyKey)
                .getResultList();
    }

    public List<String> getProductCategoryOptions(Integer companyKey) {
        String sql = """
        SELECT DISTINCT
            p.category
        FROM dim_product p
        JOIN fact_sales_line sl ON sl.product_key = p.product_key
        WHERE sl.company_key = :companyKey
          AND p.category IS NOT NULL
          AND TRIM(p.category) <> ''
        ORDER BY p.category
    """;

        return entityManager.createNativeQuery(sql)
                .setParameter("companyKey", companyKey)
                .getResultList();
    }
}
