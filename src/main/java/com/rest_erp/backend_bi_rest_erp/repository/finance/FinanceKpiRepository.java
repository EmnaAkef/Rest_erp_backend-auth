package com.rest_erp.backend_bi_rest_erp.repository.finance;
import com.rest_erp.backend_bi_rest_erp.dto.finance.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
@Repository
public class FinanceKpiRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public FinanceKpiRepository(
            JdbcTemplate jdbcTemplate,
            NamedParameterJdbcTemplate namedParameterJdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private BigDecimal queryForBigDecimalNamed(StringBuilder sql, MapSqlParameterSource params) {
        BigDecimal result = namedParameterJdbcTemplate.queryForObject(
                sql.toString(),
                params,
                BigDecimal.class
        );

        return result != null ? result : BigDecimal.ZERO;
    }

    private Integer queryForIntegerNamed(StringBuilder sql, MapSqlParameterSource params) {
        Integer result = namedParameterJdbcTemplate.queryForObject(
                sql.toString(),
                params,
                Integer.class
        );

        return result != null ? result : 0;
    }

    private void appendCustomerFilters(
            StringBuilder sql,
            MapSqlParameterSource params,
            String customerAlias,
            String statusAlias,
            String amountExpression,
            FinanceFilterRequest filters
    ) {
        if (filters == null) {
            return;
        }

        if (hasText(filters.getCustomerName())) {
            sql.append("""
            AND LOWER(COALESCE(
                NULLIF(TRIM(%s.organization_name), ''),
                NULLIF(TRIM(%s.contact_name), ''),
                'Unknown Customer'
            )) = LOWER(:customerName)
        """.formatted(customerAlias, customerAlias));
            params.addValue("customerName", filters.getCustomerName());
        }

        if (hasText(filters.getCustomerCategory())) {
            sql.append(" AND LOWER(%s.client_category) = LOWER(:customerCategory) ".formatted(customerAlias));
            params.addValue("customerCategory", filters.getCustomerCategory());
        }

        if (hasText(filters.getInvoiceStatus())) {
            sql.append(" AND UPPER(%s.status_code) = UPPER(:invoiceStatus) ".formatted(statusAlias));
            params.addValue("invoiceStatus", filters.getInvoiceStatus());
        }

        if (hasText(filters.getStatusGroup())) {
            sql.append(" AND UPPER(%s.status_group) = UPPER(:statusGroup) ".formatted(statusAlias));
            params.addValue("statusGroup", filters.getStatusGroup());
        }

        if (filters.getMinAmount() != null) {
            sql.append(" AND ").append(amountExpression).append(" >= :minAmount ");
            params.addValue("minAmount", filters.getMinAmount());
        }

        if (filters.getMaxAmount() != null) {
            sql.append(" AND ").append(amountExpression).append(" <= :maxAmount ");
            params.addValue("maxAmount", filters.getMaxAmount());
        }
    }

    private void appendVendorFilters(
            StringBuilder sql,
            MapSqlParameterSource params,
            String vendorAlias,
            String statusAlias,
            String amountExpression,
            FinanceFilterRequest filters
    ) {
        if (filters == null) {
            return;
        }

        if (hasText(filters.getVendorName())) {
            sql.append(" AND LOWER(%s.vendor_name) = LOWER(:vendorName) ".formatted(vendorAlias));
            params.addValue("vendorName", filters.getVendorName());
        }

        if (hasText(filters.getVendorIndustry())) {
            sql.append(" AND LOWER(%s.industry) = LOWER(:vendorIndustry) ".formatted(vendorAlias));
            params.addValue("vendorIndustry", filters.getVendorIndustry());
        }

        if (hasText(filters.getInvoiceStatus())) {
            sql.append(" AND UPPER(%s.status_code) = UPPER(:invoiceStatus) ".formatted(statusAlias));
            params.addValue("invoiceStatus", filters.getInvoiceStatus());
        }

        if (hasText(filters.getStatusGroup())) {
            sql.append(" AND UPPER(%s.status_group) = UPPER(:statusGroup) ".formatted(statusAlias));
            params.addValue("statusGroup", filters.getStatusGroup());
        }

        if (filters.getMinAmount() != null) {
            sql.append(" AND ").append(amountExpression).append(" >= :minAmount ");
            params.addValue("minAmount", filters.getMinAmount());
        }

        if (filters.getMaxAmount() != null) {
            sql.append(" AND ").append(amountExpression).append(" <= :maxAmount ");
            params.addValue("maxAmount", filters.getMaxAmount());
        }
    }

    private void appendAccountFilters(
            StringBuilder sql,
            MapSqlParameterSource params,
            String accountAlias,
            String amountExpression,
            FinanceFilterRequest filters
    ) {
        if (filters == null) {
            return;
        }

        if (hasText(filters.getAccountName())) {
            sql.append(" AND LOWER(%s.account_name) = LOWER(:accountName) ".formatted(accountAlias));
            params.addValue("accountName", filters.getAccountName());
        }

        if (hasText(filters.getAccountType())) {
            sql.append(" AND LOWER(%s.account_type) = LOWER(:accountType) ".formatted(accountAlias));
            params.addValue("accountType", filters.getAccountType());
        }

        if (hasText(filters.getTransactionType())) {
            sql.append(" AND LOWER(%s.transaction_type) = LOWER(:transactionType) ".formatted(accountAlias));
            params.addValue("transactionType", filters.getTransactionType());
        }

        if (filters.getMinAmount() != null) {
            sql.append(" AND ").append(amountExpression).append(" >= :minAmount ");
            params.addValue("minAmount", filters.getMinAmount());
        }

        if (filters.getMaxAmount() != null) {
            sql.append(" AND ").append(amountExpression).append(" <= :maxAmount ");
            params.addValue("maxAmount", filters.getMaxAmount());
        }
    }

    private void appendAssetFilters(
            StringBuilder sql,
            MapSqlParameterSource params,
            String assetTypeAlias,
            String amountExpression,
            FinanceFilterRequest filters
    ) {
        if (filters == null) {
            return;
        }

        if (hasText(filters.getAssetType())) {
            sql.append(" AND LOWER(%s.asset_type) = LOWER(:assetType) ".formatted(assetTypeAlias));
            params.addValue("assetType", filters.getAssetType());
        }

        if (filters.getMinAmount() != null) {
            sql.append(" AND ").append(amountExpression).append(" >= :minAmount ");
            params.addValue("minAmount", filters.getMinAmount());
        }

        if (filters.getMaxAmount() != null) {
            sql.append(" AND ").append(amountExpression).append(" <= :maxAmount ");
            params.addValue("maxAmount", filters.getMaxAmount());
        }
    }
    private BigDecimal queryForBigDecimal(String sql, Object... params) {
        BigDecimal result = jdbcTemplate.queryForObject(sql, BigDecimal.class, params);
        return result != null ? result : BigDecimal.ZERO;
    }

    private Integer queryForInteger(String sql, Object... params) {
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, params);
        return result != null ? result : 0;
    }

    public BigDecimal getTotalRevenue(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(fi.total), 0)
        FROM fact_invoice fi
        LEFT JOIN dim_customer c ON fi.customer_key = c.customer_key
        LEFT JOIN dim_invoice_status s ON fi.status_key = s.status_key
        WHERE fi.company_key = :companyKey
          AND fi.date_key BETWEEN :startDateKey AND :endDateKey
    """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        appendCustomerFilters(sql, params, "c", "s", "fi.total", filters);

        return queryForBigDecimalNamed(sql, params);
    }

    public BigDecimal getTotalExpenses(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(fb.total), 0)
        FROM fact_bill fb
        LEFT JOIN dim_vendor v ON fb.vendor_key = v.vendor_key
        LEFT JOIN dim_invoice_status s ON fb.status_key = s.status_key
        WHERE fb.company_key = :companyKey
          AND fb.date_key BETWEEN :startDateKey AND :endDateKey
    """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        appendVendorFilters(sql, params, "v", "s", "fb.total", filters);

        return queryForBigDecimalNamed(sql, params);
    }

    public BigDecimal getCashBalance(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(f.close_balance_debit - f.close_balance_credit), 0)
        FROM fact_chart_balance_snapshot f
        JOIN dim_chart_account a ON f.chart_account_key = a.chart_key
        WHERE f.company_key = :companyKey
          AND f.date_key BETWEEN :startDateKey AND :endDateKey
          AND a.is_current = true
          AND LOWER(a.account_type) IN (
                'cash and cash equivalents',
                'bank balance'
          )
    """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        appendAccountFilters(
                sql,
                params,
                "a",
                "ABS(f.close_balance_debit - f.close_balance_credit)",
                filters
        );

        return queryForBigDecimalNamed(sql, params);
    }

    public BigDecimal getBankAccountBalance(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(f.close_balance_debit - f.close_balance_credit), 0)
        FROM fact_chart_balance_snapshot f
        JOIN dim_chart_account a ON f.chart_account_key = a.chart_key
        WHERE f.company_key = :companyKey
          AND f.date_key BETWEEN :startDateKey AND :endDateKey
          AND a.is_current = true
          AND LOWER(a.account_type) = 'bank balance'
    """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        appendAccountFilters(
                sql,
                params,
                "a",
                "ABS(f.close_balance_debit - f.close_balance_credit)",
                filters
        );

        return queryForBigDecimalNamed(sql, params);
    }

    public BigDecimal getTotalLiabilities(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(f.close_balance_credit - f.close_balance_debit), 0)
        FROM fact_chart_balance_snapshot f
        JOIN dim_chart_account a ON f.chart_account_key = a.chart_key
        WHERE f.company_key = :companyKey
          AND f.date_key BETWEEN :startDateKey AND :endDateKey
          AND a.is_current = true
          AND LOWER(a.account_type) IN (
                'current liability',
                'liability',
                'accrued salaries',
                'accrued',
                'gosi allowance - employee'
          )
    """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        appendAccountFilters(
                sql,
                params,
                "a",
                "ABS(f.close_balance_credit - f.close_balance_debit)",
                filters
        );

        return queryForBigDecimalNamed(sql, params);
    }

    public BigDecimal getAccountsReceivable(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(fi.total - COALESCE(fi.partial_paid_amount, 0)), 0)
        FROM fact_invoice fi
        LEFT JOIN dim_customer c ON fi.customer_key = c.customer_key
        LEFT JOIN dim_invoice_status s ON fi.status_key = s.status_key
        WHERE fi.company_key = :companyKey
          AND fi.date_key BETWEEN :startDateKey AND :endDateKey
    """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        appendCustomerFilters(
                sql,
                params,
                "c",
                "s",
                "COALESCE(fi.total - COALESCE(fi.partial_paid_amount, 0), 0)",
                filters
        );

        return queryForBigDecimalNamed(sql, params);
    }

    public BigDecimal getAccountsPayable(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(fb.total), 0)
        FROM fact_bill fb
        JOIN dim_invoice_status s ON fb.status_key = s.status_key
        LEFT JOIN dim_vendor v ON fb.vendor_key = v.vendor_key
        WHERE fb.company_key = :companyKey
          AND fb.date_key BETWEEN :startDateKey AND :endDateKey
          AND s.status_code IN (
                'UNPAID',
                'WAITING',
                'ACCEPTED',
                'PARTIALLY_PAID'
          )
    """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        appendVendorFilters(sql, params, "v", "s", "fb.total", filters);

        return queryForBigDecimalNamed(sql, params);
    }

    public Integer getNumberOfOpenInvoices(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(fi.invoice_id)
        FROM fact_invoice fi
        JOIN dim_invoice_status s ON fi.status_key = s.status_key
        LEFT JOIN dim_customer c ON fi.customer_key = c.customer_key
        WHERE fi.company_key = :companyKey
          AND fi.date_key BETWEEN :startDateKey AND :endDateKey
          AND s.status_code IN (
                'UNPAID',
                'WAITING',
                'ACCEPTED',
                'PARTIALLY_PAID'
          )
    """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        appendCustomerFilters(sql, params, "c", "s", "fi.total", filters);

        return queryForIntegerNamed(sql, params);
    }

    public Integer getDueInvoices(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT COUNT(fi.invoice_id)
        FROM fact_invoice fi
        JOIN dim_invoice_status s ON fi.status_key = s.status_key
        JOIN dim_date d ON fi.due_date_key = d.date_key
        LEFT JOIN dim_customer c ON fi.customer_key = c.customer_key
        WHERE fi.company_key = :companyKey
          AND fi.date_key BETWEEN :startDateKey AND :endDateKey
          AND s.status_code IN (
                'UNPAID',
                'WAITING',
                'ACCEPTED',
                'PARTIALLY_PAID'
          )
          AND d.full_date < CURRENT_DATE
    """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        appendCustomerFilters(sql, params, "c", "s", "fi.total", filters);

        return queryForIntegerNamed(sql, params);
    }

    public BigDecimal getAssetValue(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(f.asset_value), 0)
        FROM fact_asset f
        LEFT JOIN dim_asset_type t ON f.asset_type_key = t.asset_type_key
        WHERE f.company_key = :companyKey
          AND f.date_key BETWEEN :startDateKey AND :endDateKey
    """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        appendAssetFilters(sql, params, "t", "f.asset_value", filters);

        return queryForBigDecimalNamed(sql, params);
    }

    public BigDecimal getDepreciationExpense(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(f.depreciation_amount), 0)
        FROM fact_asset f
        LEFT JOIN dim_asset_type t ON f.asset_type_key = t.asset_type_key
        WHERE f.company_key = :companyKey
          AND f.date_key BETWEEN :startDateKey AND :endDateKey
    """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        appendAssetFilters(sql, params, "t", "f.depreciation_amount", filters);

        return queryForBigDecimalNamed(sql, params);
    }

    public BigDecimal getVatCollected(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(fi.tax), 0)
        FROM fact_invoice fi
        JOIN dim_invoice_status s ON fi.status_key = s.status_key
        LEFT JOIN dim_customer c ON fi.customer_key = c.customer_key
        WHERE fi.company_key = :companyKey
          AND fi.date_key BETWEEN :startDateKey AND :endDateKey
          AND s.status_code = 'PAID'
    """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        appendCustomerFilters(sql, params, "c", "s", "fi.tax", filters);

        return queryForBigDecimalNamed(sql, params);
    }
    public BigDecimal getCurrentLiabilities(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(f.close_balance_credit - f.close_balance_debit), 0)
        FROM fact_chart_balance_snapshot f
        JOIN dim_chart_account a ON f.chart_account_key = a.chart_key
        WHERE f.company_key = :companyKey
          AND f.date_key BETWEEN :startDateKey AND :endDateKey
          AND a.is_current = true
          AND LOWER(a.account_type) IN (
                'current liability',
                'accrued salaries',
                'accrued'
          )
    """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        appendAccountFilters(
                sql,
                params,
                "a",
                "ABS(f.close_balance_credit - f.close_balance_debit)",
                filters
        );

        return queryForBigDecimalNamed(sql, params);
    }
    public BigDecimal getVatFromBills(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(fb.tax), 0)
        FROM fact_bill fb
        LEFT JOIN dim_vendor v ON fb.vendor_key = v.vendor_key
        LEFT JOIN dim_invoice_status s ON fb.status_key = s.status_key
        WHERE fb.company_key = :companyKey
          AND fb.date_key BETWEEN :startDateKey AND :endDateKey
    """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        appendVendorFilters(sql, params, "v", "s", "fb.tax", filters);

        return queryForBigDecimalNamed(sql, params);
    }

    public List<FinanceRevenueProfitTrendItem> getRevenueProfitTrend(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        WITH revenue_by_month AS (
            SELECT
                d.year,
                d.month,
                MIN(d.full_date) AS month_date,
                COALESCE(SUM(fi.total), 0) AS revenue
            FROM dim_date d
            LEFT JOIN fact_invoice fi
                ON fi.date_key = d.date_key
               AND fi.company_key = :companyKey
            LEFT JOIN dim_customer c
                ON fi.customer_key = c.customer_key
            LEFT JOIN dim_invoice_status si
                ON fi.status_key = si.status_key
            WHERE d.date_key BETWEEN :startDateKey AND :endDateKey
        """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        appendCustomerFilters(sql, params, "c", "si", "fi.total", filters);

        sql.append("""
            GROUP BY d.year, d.month
        ),
        expenses_by_month AS (
            SELECT
                d.year,
                d.month,
                COALESCE(SUM(fb.total), 0) AS expenses
            FROM dim_date d
            LEFT JOIN fact_bill fb
                ON fb.date_key = d.date_key
               AND fb.company_key = :companyKey
            LEFT JOIN dim_vendor v
                ON fb.vendor_key = v.vendor_key
            LEFT JOIN dim_invoice_status sb
                ON fb.status_key = sb.status_key
            WHERE d.date_key BETWEEN :startDateKey AND :endDateKey
        """);

        appendVendorFilters(sql, params, "v", "sb", "fb.total", filters);

        sql.append("""
            GROUP BY d.year, d.month
        )
        SELECT
            TO_CHAR(r.month_date, 'Mon YYYY') AS period,
            r.revenue AS revenue,
            r.revenue - e.expenses AS profit
        FROM revenue_by_month r
        JOIN expenses_by_month e
            ON r.year = e.year
           AND r.month = e.month
        ORDER BY r.year, r.month
    """);

        return namedParameterJdbcTemplate.query(
                sql.toString(),
                params,
                (rs, rowNum) -> new FinanceRevenueProfitTrendItem(
                        rs.getString("period"),
                        rs.getBigDecimal("revenue"),
                        rs.getBigDecimal("profit")
                )
        );
    }
    public List<FinanceCashFlowTrendItem> getCashFlowTrend(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT
            TO_CHAR(d.full_date, 'DD Mon') AS period,
            COALESCE(SUM(f.debit), 0) AS inflow,
            COALESCE(SUM(f.credit), 0) AS outflow,
            COALESCE(SUM(f.debit - f.credit), 0) AS net_cash_flow
        FROM fact_cash_movement f
        JOIN dim_date d
            ON f.date_key = d.date_key
        JOIN dim_chart_account a
            ON f.chart_account_key = a.chart_key
        WHERE f.company_key = :companyKey
          AND f.date_key BETWEEN :startDateKey AND :endDateKey
          AND a.is_current = true
          AND LOWER(a.account_type) IN (
                'cash and cash equivalents',
                'bank balance'
          )
    """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        appendAccountFilters(
                sql,
                params,
                "a",
                "ABS(f.debit - f.credit)",
                filters
        );

        sql.append("""
        GROUP BY d.full_date
        ORDER BY d.full_date
    """);

        return namedParameterJdbcTemplate.query(
                sql.toString(),
                params,
                (rs, rowNum) -> new FinanceCashFlowTrendItem(
                        rs.getString("period"),
                        rs.getBigDecimal("inflow"),
                        rs.getBigDecimal("outflow"),
                        rs.getBigDecimal("net_cash_flow")
                )
        );
    }
    public List<FinanceOutstandingInvoiceItem> getTopOutstandingInvoices(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT
            COALESCE(c.organization_name, c.contact_name, 'Unknown Customer') AS client,
            COALESCE(CAST(fi.invoicenumber AS TEXT), CAST(fi.invoice_id AS TEXT)) AS reference,
            COALESCE(fi.total - COALESCE(fi.partial_paid_amount, 0), 0) AS amount,
            d.full_date AS due_date,
            CASE
                WHEN d.full_date < CURRENT_DATE THEN 'Overdue'
                WHEN d.full_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 days' THEN 'Due Soon'
                ELSE s.status_code
            END AS status
        FROM fact_invoice fi
        LEFT JOIN dim_customer c
            ON fi.customer_key = c.customer_key
        LEFT JOIN dim_invoice_status s
            ON fi.status_key = s.status_key
        LEFT JOIN dim_date d
            ON fi.due_date_key = d.date_key
        WHERE fi.company_key = :companyKey
          AND fi.date_key BETWEEN :startDateKey AND :endDateKey
          AND s.status_code IN (
                'UNPAID',
                'WAITING',
                'ACCEPTED',
                'PARTIALLY_PAID'
          )
          AND COALESCE(fi.total - COALESCE(fi.partial_paid_amount, 0), 0) > 0
        """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        if (filters != null) {
            if (filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
                sql.append("""
                AND LOWER(COALESCE(c.organization_name, c.contact_name, ''))
                    LIKE LOWER(CONCAT('%', :clientVendor, '%'))
                """);
                params.addValue("clientVendor", filters.getCustomerName());
            }

            if (filters.getInvoiceStatus() != null && !filters.getInvoiceStatus().isBlank()) {
                if ("Overdue".equalsIgnoreCase(filters.getInvoiceStatus())) {
                    sql.append(" AND d.full_date < CURRENT_DATE ");
                } else if ("Due Soon".equalsIgnoreCase(filters.getInvoiceStatus())) {
                    sql.append(" AND d.full_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 days' ");
                } else {
                    sql.append(" AND UPPER(s.status_code) = UPPER(:invoiceStatus) ");
                    params.addValue("invoiceStatus", filters.getInvoiceStatus());
                }
            }

            if (filters.getMinAmount() != null) {
                sql.append("""
                AND COALESCE(fi.total - COALESCE(fi.partial_paid_amount, 0), 0) >= :minAmount
                """);
                params.addValue("minAmount", filters.getMinAmount());
            }

            if (filters.getMaxAmount() != null) {
                sql.append("""
                AND COALESCE(fi.total - COALESCE(fi.partial_paid_amount, 0), 0) <= :maxAmount
                """);
                params.addValue("maxAmount", filters.getMaxAmount());
            }
        }

        sql.append("""
        ORDER BY amount DESC
        LIMIT 5
        """);

        return namedParameterJdbcTemplate.query(
                sql.toString(),
                params,
                (rs, rowNum) -> new FinanceOutstandingInvoiceItem(
                        rs.getString("client"),
                        rs.getString("reference"),
                        rs.getBigDecimal("amount"),
                        rs.getDate("due_date") != null ? rs.getDate("due_date").toLocalDate() : null,
                        rs.getString("status")
                )
        );
    }
    public FinanceLiabilityAssetItem getLiabilityVsAssets(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        WITH assets AS (
            SELECT
                0 AS current_assets,
                COALESCE(SUM(f.asset_value), 0) AS fixed_assets,
                COALESCE(SUM(f.asset_value), 0) AS total_assets
            FROM fact_asset f
            LEFT JOIN dim_asset_type t
                ON f.asset_type_key = t.asset_type_key
            WHERE f.company_key = :companyKey
              AND f.date_key <= :endDateKey
    """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);

        appendAssetFilters(sql, params, "t", "f.asset_value", filters);

        sql.append("""
        ),
        liabilities AS (
            SELECT
                COALESCE(SUM(
                    CASE
                        WHEN LOWER(a.account_type) IN (
                            'current liability',
                            'accrued',
                            'accrued salaries'
                        )
                        THEN cb.close_balance_credit - cb.close_balance_debit
                        ELSE 0
                    END
                ), 0) AS current_liabilities,

                COALESCE(SUM(
                    CASE
                        WHEN LOWER(a.account_type) IN (
                            'liability',
                            'gosi allowance - employee'
                        )
                        THEN cb.close_balance_credit - cb.close_balance_debit
                        ELSE 0
                    END
                ), 0) AS long_term_liabilities,

                COALESCE(SUM(
                    CASE
                        WHEN LOWER(a.account_type) IN (
                            'current liability',
                            'accrued',
                            'accrued salaries',
                            'liability',
                            'gosi allowance - employee'
                        )
                        THEN cb.close_balance_credit - cb.close_balance_debit
                        ELSE 0
                    END
                ), 0) AS total_liabilities
            FROM fact_chart_balance_snapshot cb
            JOIN dim_chart_account a
                ON cb.chart_account_key = a.chart_key
            WHERE cb.company_key = :companyKey
              AND cb.date_key BETWEEN :startDateKey AND :endDateKey
              AND a.is_current = true
    """);

        appendAccountFilters(
                sql,
                params,
                "a",
                "ABS(cb.close_balance_credit - cb.close_balance_debit)",
                filters
        );

        sql.append("""
        )
        SELECT
            assets.current_assets,
            assets.fixed_assets,
            assets.total_assets,
            liabilities.current_liabilities,
            liabilities.long_term_liabilities,
            liabilities.total_liabilities
        FROM assets, liabilities
    """);

        return namedParameterJdbcTemplate.queryForObject(
                sql.toString(),
                params,
                (rs, rowNum) -> new FinanceLiabilityAssetItem(
                        rs.getBigDecimal("current_assets"),
                        rs.getBigDecimal("fixed_assets"),
                        rs.getBigDecimal("total_assets"),
                        rs.getBigDecimal("current_liabilities"),
                        rs.getBigDecimal("long_term_liabilities"),
                        rs.getBigDecimal("total_liabilities")
                )
        );
    }
    public List<FinanceAssetDistributionItem> getAssetDistribution(
            Integer companyKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT
            COALESCE(t.asset_type, 'Unknown Asset Type') AS asset_type,
            COALESCE(SUM(f.asset_value), 0) AS asset_value
        FROM fact_asset f
        LEFT JOIN dim_asset_type t
            ON f.asset_type_key = t.asset_type_key
        WHERE f.company_key = :companyKey
          AND f.date_key <= :endDateKey
        """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("endDateKey", endDateKey);

        if (filters != null) {
            if (filters.getAssetType() != null && !filters.getAssetType().isBlank()) {
                sql.append("""
                AND LOWER(COALESCE(t.asset_type, ''))
                    = LOWER(:assetType)
                """);
                params.addValue("assetType", filters.getAssetType());
            }
        }

        sql.append("""
        GROUP BY COALESCE(t.asset_type, 'Unknown Asset Type')
        HAVING 1 = 1
        """);

        if (filters != null) {
            if (filters.getMinAmount() != null) {
                sql.append(" AND COALESCE(SUM(f.asset_value), 0) >= :minAmount ");
                params.addValue("minAmount", filters.getMinAmount());
            }

            if (filters.getMaxAmount() != null) {
                sql.append(" AND COALESCE(SUM(f.asset_value), 0) <= :maxAmount ");
                params.addValue("maxAmount", filters.getMaxAmount());
            }
        }

        sql.append(" ORDER BY asset_value DESC ");

        return namedParameterJdbcTemplate.query(
                sql.toString(),
                params,
                (rs, rowNum) -> new FinanceAssetDistributionItem(
                        rs.getString("asset_type"),
                        rs.getBigDecimal("asset_value")
                )
        );
    }

    public FinanceComplianceSummaryResponse getComplianceSummary(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        BigDecimal vatCollected = getVatCollected(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        BigDecimal vatPayable = getVatFromBills(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        BigDecimal totalRevenue = getTotalRevenue(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        BigDecimal totalExpenses = getTotalExpenses(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);

        BigDecimal estimatedTax = vatPayable;

        if (netProfit.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal profitTaxEstimate = netProfit.multiply(new BigDecimal("0.05"));
            estimatedTax = estimatedTax.add(profitTaxEstimate);
        }

        String complianceStatus = vatPayable.compareTo(BigDecimal.ZERO) > 0
                ? "Pending Review"
                : "Full Compliance";

        String complianceStatusIcon = vatPayable.compareTo(BigDecimal.ZERO) > 0
                ? "warning"
                : "verified_user";

        return FinanceComplianceSummaryResponse.builder()
                .complianceStatus(complianceStatus)
                .complianceStatusIcon(complianceStatusIcon)
                .nextFilingDates(buildNextFilingDates())
                .taxPayments(List.of(
                        FinanceComplianceSummaryResponse.TaxPaymentItem.builder()
                                .code("VAT")
                                .label("VAT Collected")
                                .amount(vatCollected)
                                .build(),
                        FinanceComplianceSummaryResponse.TaxPaymentItem.builder()
                                .code("PAY")
                                .label("VAT Payable")
                                .amount(vatPayable)
                                .build(),
                        FinanceComplianceSummaryResponse.TaxPaymentItem.builder()
                                .code("EST")
                                .label("Estimated Tax")
                                .amount(estimatedTax)
                                .build()
                ))
                .build();
    }

    private List<FinanceComplianceSummaryResponse.FilingDateItem> buildNextFilingDates() {
        LocalDate today = LocalDate.now();

        LocalDate quarterlyVatReturn = today
                .plusMonths(3)
                .withDayOfMonth(15);

        LocalDate incomeTaxProvisional = today
                .plusMonths(2)
                .withDayOfMonth(28);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);

        return List.of(
                FinanceComplianceSummaryResponse.FilingDateItem.builder()
                        .label("Quarterly VAT Return")
                        .date(quarterlyVatReturn.format(formatter))
                        .build(),
                FinanceComplianceSummaryResponse.FilingDateItem.builder()
                        .label("Income Tax Provisional")
                        .date(incomeTaxProvisional.format(formatter))
                        .build()
        );
    }
}