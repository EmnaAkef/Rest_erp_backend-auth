package com.rest_erp.backend_bi_rest_erp.bi.repository.finance;

import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceAssetDistributionItem;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceCashFlowTrendItem;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceFilingDateItem;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceFilterOptionsResponse;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceFilterRequest;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceLiabilityAssetItem;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceOutstandingInvoiceItem;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceRevenueProfitTrendItem;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceTaxPaymentItem;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public class FinanceKpiRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public FinanceKpiRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private BigDecimal queryForBigDecimal(String sql, MapSqlParameterSource params) {
        BigDecimal result = jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
        return result != null ? result : BigDecimal.ZERO;
    }

    private Integer queryForInteger(String sql, MapSqlParameterSource params) {
        Integer result = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return result != null ? result : 0;
    }

    private MapSqlParameterSource baseParams(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        return new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("startDateKey", startDateKey)
                .addValue("endDateKey", endDateKey);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String customerNameExpression(String customerAlias) {
        return "COALESCE(NULLIF(TRIM(" + customerAlias + ".organization_name), ''), "
                + "NULLIF(TRIM(" + customerAlias + ".contact_name), ''), 'Unknown Customer')";
    }

    private String invoiceStatusExpression(String statusAlias, String dueDateAlias) {
        if (!hasText(dueDateAlias)) {
            return statusAlias + ".status_code";
        }

        return """
                CASE
                    WHEN %s.full_date < CURRENT_DATE
                         AND %s.status_code IN ('UNPAID', 'WAITING', 'ACCEPTED', 'PARTIALLY_PAID')
                        THEN 'Overdue'
                    WHEN %s.full_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 days'
                         AND %s.status_code IN ('UNPAID', 'WAITING', 'ACCEPTED', 'PARTIALLY_PAID')
                        THEN 'Due Soon'
                    ELSE %s.status_code
                END
                """.formatted(dueDateAlias, statusAlias, dueDateAlias, statusAlias, statusAlias);
    }

    private void appendCustomerFilters(
            StringBuilder sql,
            MapSqlParameterSource params,
            String customerAlias,
            String statusAlias,
            String dueDateAlias,
            String amountExpression,
            FinanceFilterRequest filters
    ) {
        if (filters == null) {
            return;
        }

        if (hasText(filters.getCustomerName())) {
            sql.append(" AND ").append(customerNameExpression(customerAlias)).append(" = :customerName");
            params.addValue("customerName", filters.getCustomerName().trim());
        }

        if (hasText(filters.getCustomerCategory())) {
            sql.append(" AND ").append(customerAlias).append(".client_category = :customerCategory");
            params.addValue("customerCategory", filters.getCustomerCategory().trim());
        }

        appendStatusFilters(sql, params, statusAlias, dueDateAlias, filters);
        appendAmountFilters(sql, params, amountExpression, filters);
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
            sql.append(" AND ").append(vendorAlias).append(".vendor_name = :vendorName");
            params.addValue("vendorName", filters.getVendorName().trim());
        }

        if (hasText(filters.getVendorIndustry())) {
            sql.append(" AND ").append(vendorAlias).append(".industry = :vendorIndustry");
            params.addValue("vendorIndustry", filters.getVendorIndustry().trim());
        }

        appendStatusFilters(sql, params, statusAlias, null, filters);
        appendAmountFilters(sql, params, amountExpression, filters);
    }

    private void appendStatusFilters(
            StringBuilder sql,
            MapSqlParameterSource params,
            String statusAlias,
            String dueDateAlias,
            FinanceFilterRequest filters
    ) {
        if (hasText(filters.getInvoiceStatus())) {
            sql.append(" AND ").append(invoiceStatusExpression(statusAlias, dueDateAlias)).append(" = :invoiceStatus");
            params.addValue("invoiceStatus", filters.getInvoiceStatus().trim());
        }

        if (hasText(filters.getStatusGroup())) {
            sql.append(" AND ").append(statusAlias).append(".status_group = :statusGroup");
            params.addValue("statusGroup", filters.getStatusGroup().trim());
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
            sql.append(" AND ").append(accountAlias).append(".account_name = :accountName");
            params.addValue("accountName", filters.getAccountName().trim());
        }

        if (hasText(filters.getAccountType())) {
            sql.append(" AND ").append(accountAlias).append(".account_type = :accountType");
            params.addValue("accountType", filters.getAccountType().trim());
        }

        if (hasText(filters.getTransactionType())) {
            sql.append(" AND ").append(accountAlias).append(".transaction_type = :transactionType");
            params.addValue("transactionType", filters.getTransactionType().trim());
        }

        appendAmountFilters(sql, params, amountExpression, filters);
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
            sql.append(" AND ").append(assetTypeAlias).append(".asset_type = :assetType");
            params.addValue("assetType", filters.getAssetType().trim());
        }

        appendAmountFilters(sql, params, amountExpression, filters);
    }

    private void appendAmountFilters(
            StringBuilder sql,
            MapSqlParameterSource params,
            String amountExpression,
            FinanceFilterRequest filters
    ) {
        if (filters == null || !hasText(amountExpression)) {
            return;
        }

        if (filters.getMinAmount() != null) {
            sql.append(" AND ").append(amountExpression).append(" >= :minAmount");
            params.addValue("minAmount", filters.getMinAmount());
        }

        if (filters.getMaxAmount() != null) {
            sql.append(" AND ").append(amountExpression).append(" <= :maxAmount");
            params.addValue("maxAmount", filters.getMaxAmount());
        }
    }

    public BigDecimal getTotalRevenue(Integer companyKey, Integer startDateKey, Integer endDateKey, FinanceFilterRequest filters) {
        StringBuilder sql = new StringBuilder("""
            SELECT COALESCE(SUM(fi.total), 0)
            FROM fact_invoice fi
            LEFT JOIN dim_customer c ON fi.customer_key = c.customer_key
            LEFT JOIN dim_invoice_status s ON fi.status_key = s.status_key
            LEFT JOIN dim_date due_d ON fi.due_date_key = due_d.date_key
            WHERE fi.company_key = :companyKey
              AND fi.date_key BETWEEN :startDateKey AND :endDateKey
            """);

        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);
        appendCustomerFilters(sql, params, "c", "s", "due_d", "fi.total", filters);
        return queryForBigDecimal(sql.toString(), params);
    }

    public BigDecimal getTotalExpenses(Integer companyKey, Integer startDateKey, Integer endDateKey, FinanceFilterRequest filters) {
        StringBuilder sql = new StringBuilder("""
            SELECT COALESCE(SUM(fb.total), 0)
            FROM fact_bill fb
            LEFT JOIN dim_vendor v ON fb.vendor_key = v.vendor_key
            LEFT JOIN dim_invoice_status s ON fb.status_key = s.status_key
            WHERE fb.company_key = :companyKey
              AND fb.date_key BETWEEN :startDateKey AND :endDateKey
            """);

        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);
        appendVendorFilters(sql, params, "v", "s", "fb.total", filters);
        return queryForBigDecimal(sql.toString(), params);
    }

    public BigDecimal getCashBalance(Integer companyKey, Integer startDateKey, Integer endDateKey, FinanceFilterRequest filters) {
        StringBuilder sql = new StringBuilder("""
            SELECT COALESCE(SUM(f.close_balance_debit - f.close_balance_credit), 0)
            FROM fact_chart_balance_snapshot f
            JOIN dim_chart_account a ON f.chart_account_key = a.chart_key
            WHERE f.company_key = :companyKey
              AND f.date_key BETWEEN :startDateKey AND :endDateKey
              AND a.is_current = true
              AND LOWER(a.account_type) IN ('cash and cash equivalents', 'bank balance')
            """);

        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);
        appendAccountFilters(sql, params, "a", "ABS(f.close_balance_debit - f.close_balance_credit)", filters);
        return queryForBigDecimal(sql.toString(), params);
    }

    public BigDecimal getBankAccountBalance(Integer companyKey, Integer startDateKey, Integer endDateKey, FinanceFilterRequest filters) {
        StringBuilder sql = new StringBuilder("""
            SELECT COALESCE(SUM(f.close_balance_debit - f.close_balance_credit), 0)
            FROM fact_chart_balance_snapshot f
            JOIN dim_chart_account a ON f.chart_account_key = a.chart_key
            WHERE f.company_key = :companyKey
              AND f.date_key BETWEEN :startDateKey AND :endDateKey
              AND a.is_current = true
              AND LOWER(a.account_type) = 'bank balance'
            """);

        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);
        appendAccountFilters(sql, params, "a", "ABS(f.close_balance_debit - f.close_balance_credit)", filters);
        return queryForBigDecimal(sql.toString(), params);
    }

    public BigDecimal getTotalLiabilities(Integer companyKey, Integer startDateKey, Integer endDateKey, FinanceFilterRequest filters) {
        StringBuilder sql = liabilityBalanceSql("""
              AND LOWER(a.account_type) IN (
                    'current liability',
                    'liability',
                    'accrued salaries',
                    'accrued',
                    'gosi allowance - employee'
              )
            """);

        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);
        appendAccountFilters(sql, params, "a", "ABS(f.close_balance_credit - f.close_balance_debit)", filters);
        return queryForBigDecimal(sql.toString(), params);
    }

    public BigDecimal getCurrentLiabilities(Integer companyKey, Integer startDateKey, Integer endDateKey, FinanceFilterRequest filters) {
        StringBuilder sql = liabilityBalanceSql("""
              AND LOWER(a.account_type) IN ('current liability', 'accrued salaries', 'accrued')
            """);

        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);
        appendAccountFilters(sql, params, "a", "ABS(f.close_balance_credit - f.close_balance_debit)", filters);
        return queryForBigDecimal(sql.toString(), params);
    }

    private StringBuilder liabilityBalanceSql(String accountTypeClause) {
        return new StringBuilder("""
            SELECT COALESCE(SUM(f.close_balance_credit - f.close_balance_debit), 0)
            FROM fact_chart_balance_snapshot f
            JOIN dim_chart_account a ON f.chart_account_key = a.chart_key
            WHERE f.company_key = :companyKey
              AND f.date_key BETWEEN :startDateKey AND :endDateKey
              AND a.is_current = true
            """).append(accountTypeClause);
    }

    public BigDecimal getAccountsReceivable(Integer companyKey, Integer startDateKey, Integer endDateKey, FinanceFilterRequest filters) {
        StringBuilder sql = new StringBuilder("""
            SELECT COALESCE(SUM(fi.total - COALESCE(fi.partial_paid_amount, 0)), 0)
            FROM fact_invoice fi
            LEFT JOIN dim_customer c ON fi.customer_key = c.customer_key
            LEFT JOIN dim_invoice_status s ON fi.status_key = s.status_key
            LEFT JOIN dim_date due_d ON fi.due_date_key = due_d.date_key
            WHERE fi.company_key = :companyKey
              AND fi.date_key BETWEEN :startDateKey AND :endDateKey
            """);

        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);
        appendCustomerFilters(sql, params, "c", "s", "due_d", "COALESCE(fi.total - COALESCE(fi.partial_paid_amount, 0), 0)", filters);
        return queryForBigDecimal(sql.toString(), params);
    }

    public BigDecimal getAccountsPayable(Integer companyKey, Integer startDateKey, Integer endDateKey, FinanceFilterRequest filters) {
        StringBuilder sql = new StringBuilder("""
            SELECT COALESCE(SUM(fb.total), 0)
            FROM fact_bill fb
            JOIN dim_invoice_status s ON fb.status_key = s.status_key
            LEFT JOIN dim_vendor v ON fb.vendor_key = v.vendor_key
            WHERE fb.company_key = :companyKey
              AND fb.date_key BETWEEN :startDateKey AND :endDateKey
              AND s.status_code IN ('UNPAID', 'WAITING', 'ACCEPTED', 'PARTIALLY_PAID')
            """);

        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);
        appendVendorFilters(sql, params, "v", "s", "fb.total", filters);
        return queryForBigDecimal(sql.toString(), params);
    }

    public Integer getNumberOfOpenInvoices(Integer companyKey, Integer startDateKey, Integer endDateKey, FinanceFilterRequest filters) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(fi.invoice_id)
            FROM fact_invoice fi
            JOIN dim_invoice_status s ON fi.status_key = s.status_key
            LEFT JOIN dim_customer c ON fi.customer_key = c.customer_key
            LEFT JOIN dim_date due_d ON fi.due_date_key = due_d.date_key
            WHERE fi.company_key = :companyKey
              AND fi.date_key BETWEEN :startDateKey AND :endDateKey
              AND s.status_code IN ('UNPAID', 'WAITING', 'ACCEPTED', 'PARTIALLY_PAID')
            """);

        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);
        appendCustomerFilters(sql, params, "c", "s", "due_d", "fi.total", filters);
        return queryForInteger(sql.toString(), params);
    }

    public Integer getDueInvoices(Integer companyKey, Integer startDateKey, Integer endDateKey, FinanceFilterRequest filters) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(fi.invoice_id)
            FROM fact_invoice fi
            JOIN dim_invoice_status s ON fi.status_key = s.status_key
            JOIN dim_date due_d ON fi.due_date_key = due_d.date_key
            LEFT JOIN dim_customer c ON fi.customer_key = c.customer_key
            WHERE fi.company_key = :companyKey
              AND fi.date_key BETWEEN :startDateKey AND :endDateKey
              AND s.status_code IN ('UNPAID', 'WAITING', 'ACCEPTED', 'PARTIALLY_PAID')
              AND due_d.full_date < CURRENT_DATE
            """);

        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);
        appendCustomerFilters(sql, params, "c", "s", "due_d", "fi.total", filters);
        return queryForInteger(sql.toString(), params);
    }

    public BigDecimal getAssetValue(Integer companyKey, Integer startDateKey, Integer endDateKey, FinanceFilterRequest filters) {
        StringBuilder sql = new StringBuilder("""
            SELECT COALESCE(SUM(f.asset_value), 0)
            FROM fact_asset f
            LEFT JOIN dim_asset_type t ON f.asset_type_key = t.asset_type_key
            WHERE f.company_key = :companyKey
              AND f.date_key BETWEEN :startDateKey AND :endDateKey
            """);

        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);
        appendAssetFilters(sql, params, "t", "f.asset_value", filters);
        return queryForBigDecimal(sql.toString(), params);
    }

    public BigDecimal getDepreciationExpense(Integer companyKey, Integer startDateKey, Integer endDateKey, FinanceFilterRequest filters) {
        StringBuilder sql = new StringBuilder("""
            SELECT COALESCE(SUM(f.depreciation_amount), 0)
            FROM fact_asset f
            LEFT JOIN dim_asset_type t ON f.asset_type_key = t.asset_type_key
            WHERE f.company_key = :companyKey
              AND f.date_key BETWEEN :startDateKey AND :endDateKey
            """);

        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);
        appendAssetFilters(sql, params, "t", "f.depreciation_amount", filters);
        return queryForBigDecimal(sql.toString(), params);
    }

    public BigDecimal getVatCollected(Integer companyKey, Integer startDateKey, Integer endDateKey, FinanceFilterRequest filters) {
        StringBuilder sql = new StringBuilder("""
            SELECT COALESCE(SUM(fi.tax), 0)
            FROM fact_invoice fi
            JOIN dim_invoice_status s ON fi.status_key = s.status_key
            LEFT JOIN dim_customer c ON fi.customer_key = c.customer_key
            LEFT JOIN dim_date due_d ON fi.due_date_key = due_d.date_key
            WHERE fi.company_key = :companyKey
              AND fi.date_key BETWEEN :startDateKey AND :endDateKey
              AND s.status_code = 'PAID'
            """);

        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);
        appendCustomerFilters(sql, params, "c", "s", "due_d", "fi.tax", filters);
        return queryForBigDecimal(sql.toString(), params);
    }

    public BigDecimal getVatFromBills(Integer companyKey, Integer startDateKey, Integer endDateKey, FinanceFilterRequest filters) {
        StringBuilder sql = new StringBuilder("""
            SELECT COALESCE(SUM(fb.tax), 0)
            FROM fact_bill fb
            LEFT JOIN dim_vendor v ON fb.vendor_key = v.vendor_key
            LEFT JOIN dim_invoice_status s ON fb.status_key = s.status_key
            WHERE fb.company_key = :companyKey
              AND fb.date_key BETWEEN :startDateKey AND :endDateKey
            """);

        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);
        appendVendorFilters(sql, params, "v", "s", "fb.tax", filters);
        return queryForBigDecimal(sql.toString(), params);
    }

    public List<FinanceRevenueProfitTrendItem> getRevenueProfitTrend(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder revenueFilters = new StringBuilder();
        StringBuilder expenseFilters = new StringBuilder();
        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);

        appendCustomerFilters(revenueFilters, params, "c", "isr", "due_d", "fi.total", filters);
        appendVendorFilters(expenseFilters, params, "v", "ise", "fb.total", filters);

        String sql = """
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
                LEFT JOIN dim_customer c ON fi.customer_key = c.customer_key
                LEFT JOIN dim_invoice_status isr ON fi.status_key = isr.status_key
                LEFT JOIN dim_date due_d ON fi.due_date_key = due_d.date_key
                WHERE d.date_key BETWEEN :startDateKey AND :endDateKey
                %s
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
                LEFT JOIN dim_vendor v ON fb.vendor_key = v.vendor_key
                LEFT JOIN dim_invoice_status ise ON fb.status_key = ise.status_key
                WHERE d.date_key BETWEEN :startDateKey AND :endDateKey
                %s
                GROUP BY d.year, d.month
            )
            SELECT
                TO_CHAR(r.month_date, 'Mon YYYY') AS period,
                r.revenue AS revenue,
                r.revenue - e.expenses AS profit
            FROM revenue_by_month r
            JOIN expenses_by_month e ON r.year = e.year AND r.month = e.month
            ORDER BY r.year, r.month
            """.formatted(revenueFilters, expenseFilters);

        return jdbcTemplate.query(
                sql,
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
            JOIN dim_date d ON f.date_key = d.date_key
            JOIN dim_chart_account a ON f.chart_account_key = a.chart_key
            WHERE f.company_key = :companyKey
              AND f.date_key BETWEEN :startDateKey AND :endDateKey
              AND a.is_current = true
              AND LOWER(a.account_type) IN ('cash and cash equivalents', 'bank balance')
            """);

        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);
        appendAccountFilters(sql, params, "a", "ABS(f.debit - f.credit)", filters);

        sql.append("""
            GROUP BY d.full_date
            ORDER BY d.full_date
            """);

        return jdbcTemplate.query(
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
            LEFT JOIN dim_customer c ON fi.customer_key = c.customer_key
            LEFT JOIN dim_invoice_status s ON fi.status_key = s.status_key
            LEFT JOIN dim_date d ON fi.due_date_key = d.date_key
            WHERE fi.company_key = :companyKey
              AND fi.date_key BETWEEN :startDateKey AND :endDateKey
              AND s.status_code IN ('UNPAID', 'WAITING', 'ACCEPTED', 'PARTIALLY_PAID')
              AND COALESCE(fi.total - COALESCE(fi.partial_paid_amount, 0), 0) > 0
            """);

        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);
        appendCustomerFilters(sql, params, "c", "s", "d", "COALESCE(fi.total - COALESCE(fi.partial_paid_amount, 0), 0)", filters);

        sql.append("""
            ORDER BY amount DESC
            LIMIT 5
            """);

        return jdbcTemplate.query(
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
        StringBuilder assetFilters = new StringBuilder();
        StringBuilder liabilityFilters = new StringBuilder();
        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);

        appendAssetFilters(assetFilters, params, "t", "f.asset_value", filters);
        appendAccountFilters(liabilityFilters, params, "a", "ABS(cb.close_balance_credit - cb.close_balance_debit)", filters);

        String sql = """
            WITH assets AS (
                SELECT
                    0 AS current_assets,
                    COALESCE(SUM(f.asset_value), 0) AS fixed_assets,
                    COALESCE(SUM(f.asset_value), 0) AS total_assets
                FROM fact_asset f
                LEFT JOIN dim_asset_type t ON f.asset_type_key = t.asset_type_key
                WHERE f.company_key = :companyKey
                  AND f.date_key <= :endDateKey
                  %s
            ),
            liabilities AS (
                SELECT
                    COALESCE(SUM(
                        CASE
                            WHEN LOWER(a.account_type) IN ('current liability', 'accrued', 'accrued salaries')
                            THEN cb.close_balance_credit - cb.close_balance_debit
                            ELSE 0
                        END
                    ), 0) AS current_liabilities,
                    COALESCE(SUM(
                        CASE
                            WHEN LOWER(a.account_type) IN ('liability', 'gosi allowance - employee')
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
                JOIN dim_chart_account a ON cb.chart_account_key = a.chart_key
                WHERE cb.company_key = :companyKey
                  AND cb.date_key BETWEEN :startDateKey AND :endDateKey
                  AND a.is_current = true
                  %s
            )
            SELECT
                assets.current_assets,
                assets.fixed_assets,
                assets.total_assets,
                liabilities.current_liabilities,
                liabilities.long_term_liabilities,
                liabilities.total_liabilities
            FROM assets, liabilities
            """.formatted(assetFilters, liabilityFilters);

        return jdbcTemplate.queryForObject(
                sql,
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
            LEFT JOIN dim_asset_type t ON f.asset_type_key = t.asset_type_key
            WHERE f.company_key = :companyKey
              AND f.date_key <= :endDateKey
            """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("companyKey", companyKey)
                .addValue("endDateKey", endDateKey);
        appendAssetFilters(sql, params, "t", "f.asset_value", filters);

        sql.append("""
            GROUP BY COALESCE(t.asset_type, 'Unknown Asset Type')
            ORDER BY asset_value DESC
            """);

        return jdbcTemplate.query(
                sql.toString(),
                params,
                (rs, rowNum) -> new FinanceAssetDistributionItem(
                        rs.getString("asset_type"),
                        rs.getBigDecimal("asset_value")
                )
        );
    }

    public List<FinanceTaxPaymentItem> getRecentTaxPayments(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey,
            FinanceFilterRequest filters
    ) {
        StringBuilder sql = new StringBuilder("""
            SELECT
                'TAX' AS code,
                COALESCE(v.vendor_name, 'Tax Payment') AS label,
                COALESCE(SUM(fb.tax), 0) AS amount
            FROM fact_bill fb
            LEFT JOIN dim_vendor v ON fb.vendor_key = v.vendor_key
            LEFT JOIN dim_invoice_status s ON fb.status_key = s.status_key
            WHERE fb.company_key = :companyKey
              AND fb.date_key BETWEEN :startDateKey AND :endDateKey
              AND COALESCE(fb.tax, 0) > 0
            """);

        MapSqlParameterSource params = baseParams(companyKey, startDateKey, endDateKey);
        appendVendorFilters(sql, params, "v", "s", "fb.tax", filters);

        sql.append("""
            GROUP BY COALESCE(v.vendor_name, 'Tax Payment')
            ORDER BY amount DESC
            LIMIT 5
            """);

        return jdbcTemplate.query(
                sql.toString(),
                params,
                (rs, rowNum) -> new FinanceTaxPaymentItem(
                        rs.getString("code"),
                        rs.getString("label"),
                        rs.getBigDecimal("amount")
                )
        );
    }

    public List<FinanceFilingDateItem> getNextFilingDates() {
        LocalDate today = LocalDate.now();
        LocalDate nextVatDate = today.plusMonths(1).withDayOfMonth(15);
        LocalDate nextIncomeTaxDate = today.plusMonths(2).withDayOfMonth(30);

        return List.of(
                new FinanceFilingDateItem("Quarterly VAT Return", nextVatDate.toString()),
                new FinanceFilingDateItem("Income Tax Provisional", nextIncomeTaxDate.toString())
        );
    }

    public FinanceFilterOptionsResponse getFinanceFilterOptions(Integer companyKey) {
        return new FinanceFilterOptionsResponse(
                getStringOptions("""
                    SELECT DISTINCT COALESCE(
                        NULLIF(TRIM(organization_name), ''),
                        NULLIF(TRIM(contact_name), '')
                    ) AS value
                    FROM dim_customer
                    WHERE company_key = :companyKey
                      AND is_current = true
                      AND COALESCE(NULLIF(TRIM(organization_name), ''), NULLIF(TRIM(contact_name), '')) IS NOT NULL
                    ORDER BY value
                    """, companyKey),
                getStringOptions("""
                    SELECT DISTINCT client_category AS value
                    FROM dim_customer
                    WHERE company_key = :companyKey
                      AND is_current = true
                      AND client_category IS NOT NULL
                      AND TRIM(client_category) <> ''
                    ORDER BY value
                    """, companyKey),
                getStringOptions("""
                    SELECT DISTINCT vendor_name AS value
                    FROM dim_vendor
                    WHERE company_key = :companyKey
                      AND is_current = true
                      AND vendor_name IS NOT NULL
                      AND TRIM(vendor_name) <> ''
                    ORDER BY value
                    """, companyKey),
                getStringOptions("""
                    SELECT DISTINCT industry AS value
                    FROM dim_vendor
                    WHERE company_key = :companyKey
                      AND is_current = true
                      AND industry IS NOT NULL
                      AND TRIM(industry) <> ''
                    ORDER BY value
                    """, companyKey),
                getStringOptions("""
                    SELECT DISTINCT account_name AS value
                    FROM dim_chart_account
                    WHERE company_key = :companyKey
                      AND is_current = true
                      AND account_name IS NOT NULL
                      AND TRIM(account_name) <> ''
                    ORDER BY value
                    """, companyKey),
                getStringOptions("""
                    SELECT DISTINCT status_value AS value
                    FROM (
                        SELECT
                            CASE
                                WHEN d.full_date < CURRENT_DATE
                                     AND s.status_code IN ('UNPAID', 'WAITING', 'ACCEPTED', 'PARTIALLY_PAID')
                                    THEN 'Overdue'
                                WHEN d.full_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 days'
                                     AND s.status_code IN ('UNPAID', 'WAITING', 'ACCEPTED', 'PARTIALLY_PAID')
                                    THEN 'Due Soon'
                                ELSE s.status_code
                            END AS status_value
                        FROM fact_invoice fi
                        JOIN dim_invoice_status s ON fi.status_key = s.status_key
                        LEFT JOIN dim_date d ON fi.due_date_key = d.date_key
                        WHERE fi.company_key = :companyKey
                    ) q
                    WHERE status_value IS NOT NULL
                      AND TRIM(status_value) <> ''
                    ORDER BY value
                    """, companyKey),
                getStringOptions("""
                    SELECT DISTINCT status_group AS value
                    FROM (
                        SELECT s.status_group
                        FROM fact_invoice fi
                        JOIN dim_invoice_status s ON fi.status_key = s.status_key
                        WHERE fi.company_key = :companyKey
                        UNION
                        SELECT s.status_group
                        FROM fact_bill fb
                        JOIN dim_invoice_status s ON fb.status_key = s.status_key
                        WHERE fb.company_key = :companyKey
                    ) q
                    WHERE status_group IS NOT NULL
                      AND TRIM(status_group) <> ''
                    ORDER BY value
                    """, companyKey),
                getStringOptions("""
                    SELECT DISTINCT account_type AS value
                    FROM dim_chart_account
                    WHERE company_key = :companyKey
                      AND is_current = true
                      AND account_type IS NOT NULL
                      AND TRIM(account_type) <> ''
                    ORDER BY value
                    """, companyKey),
                getStringOptions("""
                    SELECT DISTINCT transaction_type AS value
                    FROM dim_chart_account
                    WHERE company_key = :companyKey
                      AND is_current = true
                      AND transaction_type IS NOT NULL
                      AND TRIM(transaction_type) <> ''
                    ORDER BY value
                    """, companyKey),
                getStringOptions("""
                    SELECT DISTINCT t.asset_type AS value
                    FROM fact_asset f
                    JOIN dim_asset_type t ON f.asset_type_key = t.asset_type_key
                    WHERE f.company_key = :companyKey
                      AND t.asset_type IS NOT NULL
                      AND TRIM(t.asset_type) <> ''
                    ORDER BY value
                    """, companyKey)
        );
    }

    private List<String> getStringOptions(String sql, Integer companyKey) {
        return jdbcTemplate.queryForList(
                sql,
                new MapSqlParameterSource("companyKey", companyKey),
                String.class
        );
    }

    public List<String> searchFinanceFilterOptions(Integer companyKey, String field, String q) {
        String selectSql = switch (field) {
            case "customerName" -> """
                SELECT DISTINCT COALESCE(NULLIF(TRIM(c.organization_name), ''), NULLIF(TRIM(c.contact_name), '')) AS value
                FROM dim_customer c
                WHERE c.company_key = :companyKey
                  AND c.is_current = true
                  AND COALESCE(NULLIF(TRIM(c.organization_name), ''), NULLIF(TRIM(c.contact_name), '')) IS NOT NULL
                  AND LOWER(COALESCE(NULLIF(TRIM(c.organization_name), ''), NULLIF(TRIM(c.contact_name), ''))) LIKE :search
                ORDER BY value
                """;
            case "customerCategory" -> searchOptionSql("dim_customer", "client_category", "is_current = true");
            case "vendorName" -> searchOptionSql("dim_vendor", "vendor_name", "is_current = true");
            case "vendorIndustry" -> searchOptionSql("dim_vendor", "industry", "is_current = true");
            case "accountName" -> searchOptionSql("dim_chart_account", "account_name", "is_current = true");
            case "accountType" -> searchOptionSql("dim_chart_account", "account_type", "is_current = true");
            case "transactionType" -> searchOptionSql("dim_chart_account", "transaction_type", "is_current = true");
            case "invoiceStatus" -> """
                SELECT DISTINCT status_value AS value
                FROM (
                    SELECT
                        CASE
                            WHEN d.full_date < CURRENT_DATE
                                 AND s.status_code IN ('UNPAID', 'WAITING', 'ACCEPTED', 'PARTIALLY_PAID')
                                THEN 'Overdue'
                            WHEN d.full_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 days'
                                 AND s.status_code IN ('UNPAID', 'WAITING', 'ACCEPTED', 'PARTIALLY_PAID')
                                THEN 'Due Soon'
                            ELSE s.status_code
                        END AS status_value
                    FROM fact_invoice fi
                    JOIN dim_invoice_status s ON fi.status_key = s.status_key
                    LEFT JOIN dim_date d ON fi.due_date_key = d.date_key
                    WHERE fi.company_key = :companyKey
                ) q
                WHERE status_value IS NOT NULL
                  AND TRIM(status_value) <> ''
                  AND LOWER(status_value) LIKE :search
                ORDER BY value
                """;
            case "statusGroup" -> """
                SELECT DISTINCT status_group AS value
                FROM (
                    SELECT s.status_group
                    FROM fact_invoice fi
                    JOIN dim_invoice_status s ON fi.status_key = s.status_key
                    WHERE fi.company_key = :companyKey
                    UNION
                    SELECT s.status_group
                    FROM fact_bill fb
                    JOIN dim_invoice_status s ON fb.status_key = s.status_key
                    WHERE fb.company_key = :companyKey
                ) q
                WHERE status_group IS NOT NULL
                  AND TRIM(status_group) <> ''
                  AND LOWER(status_group) LIKE :search
                ORDER BY value
                """;
            case "assetType" -> """
                SELECT DISTINCT t.asset_type AS value
                FROM fact_asset f
                JOIN dim_asset_type t ON f.asset_type_key = t.asset_type_key
                WHERE f.company_key = :companyKey
                  AND t.asset_type IS NOT NULL
                  AND TRIM(t.asset_type) <> ''
                  AND LOWER(t.asset_type) LIKE :search
                ORDER BY value
                """;
            default -> null;
        };

        if (selectSql == null) {
            return List.of();
        }

        return jdbcTemplate.queryForList(
                selectSql,
                new MapSqlParameterSource()
                        .addValue("companyKey", companyKey)
                        .addValue("search", (q == null ? "" : q.trim().toLowerCase()) + "%"),
                String.class
        );
    }

    private String searchOptionSql(String tableName, String columnName, String extraCondition) {
        return """
            SELECT DISTINCT %1$s AS value
            FROM %2$s
            WHERE company_key = :companyKey
              AND %3$s
              AND %1$s IS NOT NULL
              AND TRIM(%1$s) <> ''
              AND LOWER(%1$s) LIKE :search
            ORDER BY value
            """.formatted(columnName, tableName, extraCondition);
    }
}
