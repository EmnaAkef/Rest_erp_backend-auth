package com.rest_erp.backend_bi_rest_erp.repository.sales;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public class SalesKpiRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public BigDecimal getTotalRevenue(Integer companyKey, LocalDate startDate, LocalDate endDate) {

        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(f.allocated_amount), 0) + COALESCE(SUM(f.invoice_total), 0)
        FROM fact_sales_financials f
        JOIN dim_date d ON d.date_key = f.date_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        return result != null ? (BigDecimal) result : BigDecimal.ZERO;
    }

    public Long getNumberOfDeals(Integer companyKey, LocalDate startDate, LocalDate endDate) {

        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(DISTINCT f.deal_id)
        FROM fact_deal f
        JOIN dim_date d ON d.date_key = f.close_date_key
        WHERE f.company_key = :companyKey
        """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        if (result == null) {
            return 0L;
        }

        return ((Number) result).longValue();
    }

    public BigDecimal getWinRate(Integer companyKey, LocalDate startDate, LocalDate endDate) {

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

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        if (result == null) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(result.toString());
    }

    public BigDecimal getAverageDealValue(Integer companyKey, LocalDate startDate, LocalDate endDate) {

        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(AVG(f.deal_value), 0)
        FROM fact_deal f
        LEFT JOIN dim_date d ON d.date_key = f.close_date_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        if (result == null) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(result.toString());
    }

    public Long getSalesOrdersCount(Integer companyKey, LocalDate startDate, LocalDate endDate) {

        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(DISTINCT f.sell_order_id)
        FROM fact_sales_order f
        LEFT JOIN dim_date d ON d.date_key = f.date_key
        WHERE f.company_key = :companyKey
        """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        if (result == null) {
            return 0L;
        }

        return ((Number) result).longValue();
    }

    public BigDecimal getOutstandingReceivables(Integer companyKey, LocalDate startDate, LocalDate endDate) {

        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(f.total), 0)
        FROM fact_invoice f
        JOIN dim_invoice_status s ON s.status_key = f.status_key
        LEFT JOIN dim_date d ON d.date_key = f.date_key
        WHERE f.company_key = :companyKey
          AND s.status_group = 'PENDING'
        """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        if (result == null) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(result.toString());
    }

    public Long getPipelineDealsCount(Integer companyKey) {

        String sql = """
        SELECT COUNT(DISTINCT f.deal_id)
        FROM fact_deal f
        JOIN dim_workstatus w ON w.workstatus_key = f.workstatus_key
        WHERE f.company_key = :companyKey
          AND COALESCE(f.is_archived, false) = false
          AND w.status_label IN ('Generated', 'Initial Contact')
    """;

        Object result = entityManager.createNativeQuery(sql)
                .setParameter("companyKey", companyKey)
                .getSingleResult();

        return result != null ? ((Number) result).longValue() : 0L;
    }

    public BigDecimal getPipelineValue(Integer companyKey) {

        String sql = """
        SELECT COALESCE(SUM(f.deal_value), 0)
        FROM fact_deal f
        JOIN dim_workstatus w ON w.workstatus_key = f.workstatus_key
        WHERE f.company_key = :companyKey
          AND COALESCE(f.is_archived, false) = false
          AND w.status_label IN ('Generated', 'Initial Contact')
    """;

        Object result = entityManager.createNativeQuery(sql)
                .setParameter("companyKey", companyKey)
                .getSingleResult();

        return result != null ? new BigDecimal(result.toString()) : BigDecimal.ZERO;
    }

    public Long getActiveCustomers(Integer companyKey, LocalDate startDate, LocalDate endDate) {

        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(DISTINCT f.customer_key)
        FROM fact_invoice f
        JOIN dim_invoice_status s ON s.status_key = f.status_key
        JOIN dim_date d ON d.date_key = f.date_key
        WHERE f.company_key = :companyKey
          AND s.status_group = 'VALIDATED'
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        if (result == null) {
            return 0L;
        }

        return ((Number) result).longValue();
    }

    public Long getTotalCustomers(Integer companyKey) {

        String sql = """
        SELECT COUNT(DISTINCT c.customer_key)
        FROM dim_customer c
        WHERE c.company_key = :companyKey
        """;

        Object result = entityManager.createNativeQuery(sql)
                .setParameter("companyKey", companyKey)
                .getSingleResult();

        if (result == null) {
            return 0L;
        }

        return ((Number) result).longValue();
    }

    public BigDecimal getConversionRate(Integer companyKey, LocalDate startDate, LocalDate endDate) {

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
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        if (result == null) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(result.toString());
    }

    public java.util.List<Object[]> getRevenueTrend(Integer companyKey, LocalDate startDate, LocalDate endDate) {

        StringBuilder sql = new StringBuilder("""
        SELECT 
            TO_CHAR(DATE_TRUNC('month', d.full_date), 'Mon YYYY') AS label,
            COALESCE(SUM(f.allocated_amount), 0) + COALESCE(SUM(f.invoice_total), 0) AS value
        FROM fact_sales_financials f
        JOIN dim_date d ON d.date_key = f.date_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
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

        return query.getResultList();
    }

    public java.util.List<Object[]> getPipelineDistribution(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT 
            w.status_label,
            COUNT(DISTINCT f.deal_id) AS total_deals
        FROM fact_deal f
        JOIN dim_workstatus w 
            ON w.workstatus_key = f.workstatus_key
        JOIN dim_date d
            ON d.date_key = f.close_date_key
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
              'Lost'
          )
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
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
            WHEN 'Done' THEN 7
            WHEN 'Win' THEN 8
            WHEN 'Lost' THEN 9
            ELSE 10
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

        return query.getResultList();
    }

    public java.util.List<Object[]> getRecentSalesOrders(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate
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

        return query.getResultList();
    }

    public java.util.List<Object[]> getTopSalespersons(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT 
            COALESCE(u.user_name, 'Unknown Salesperson') AS name,
            COALESCE(SUM(f.allocated_amount), 0) + COALESCE(SUM(f.invoice_total), 0) AS amount
        FROM fact_sales_financials f
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

        return query.getResultList();
    }

    public java.util.List<Object[]> getRevenueByCustomer(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT
            COALESCE(c.organization_name, c.contact_name, 'Unknown Customer') AS customer_name,
            COALESCE(SUM(f.allocated_amount), 0) + COALESCE(SUM(f.invoice_total), 0) AS amount
        FROM fact_sales_financials f
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

        sql.append("""
        GROUP BY c.organization_name, c.contact_name
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

        return query.getResultList();
    }

    public List<Object[]> getRevenueByProduct(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate
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

        sql.append("""
        GROUP BY p.product_name
        ORDER BY amount DESC
        LIMIT 10
    """);

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) query.setParameter("startDate", startDate);
        if (endDate != null) query.setParameter("endDate", endDate);

        return query.getResultList();
    }

    public List<Object[]> getCustomerRetention(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate
    ) {
        StringBuilder sql = new StringBuilder("""
        WITH monthly_active AS (
            SELECT 
                d.year,
                d.month,
                DATE_TRUNC('month', d.full_date)::date AS month_date,
                f.customer_key
            FROM fact_sales_financials f
            JOIN dim_date d 
                ON d.date_key = f.date_key
            WHERE f.company_key = :companyKey
              AND f.customer_key IS NOT NULL
    """);

        if (startDate != null) {
            sql.append("""
              AND d.full_date >= (date_trunc('month', CAST(:startDate AS date)) - INTERVAL '1 month')::date
        """);
        }

        if (endDate != null) {
            sql.append("""
              AND d.full_date <= :endDate
        """);
        }

        sql.append("""
            GROUP BY d.year, d.month, DATE_TRUNC('month', d.full_date), f.customer_key
        ),

        monthly_counts AS (
            SELECT
                month_date,
                COUNT(DISTINCT customer_key) AS total_customers
            FROM monthly_active
            GROUP BY month_date
        ),

        retained_customers AS (
            SELECT
                curr.month_date,
                COUNT(DISTINCT curr.customer_key) AS retained_customers
            FROM monthly_active curr
            JOIN monthly_active prev
                ON curr.customer_key = prev.customer_key
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

        sql.append("""
        ORDER BY curr.month_date
    """);

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        return query.getResultList();
    }

    public List<Object[]> getHighValueDeals(Integer companyKey) {
        String sql = """
        SELECT
          deal_id,
          deal_value
      FROM fact_deal
      WHERE company_key = :companyKey
      AND deal_value IS NOT NULL
      ORDER BY deal_value DESC
      LIMIT 5
    """;

        return entityManager.createNativeQuery(sql)
                .setParameter("companyKey", companyKey)
                .getResultList();
    }
}
