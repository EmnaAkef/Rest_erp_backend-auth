package com.rest_erp.backend_bi_rest_erp.bi.repository.finance;

import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.sql.Date;
@Repository
public class FinanceKpiRepository {

    private final JdbcTemplate jdbcTemplate;

    public FinanceKpiRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private BigDecimal queryForBigDecimal(String sql, Object... params) {
        BigDecimal result = jdbcTemplate.queryForObject(sql, BigDecimal.class, params);
        return result != null ? result : BigDecimal.ZERO;
    }

    private Integer queryForInteger(String sql, Object... params) {
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, params);
        return result != null ? result : 0;
    }

    public BigDecimal getTotalRevenue(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
            SELECT COALESCE(SUM(total), 0)
            FROM fact_invoice
            WHERE company_key = ?
              AND date_key BETWEEN ? AND ?
            """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
    }

    public BigDecimal getTotalExpenses(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
            SELECT COALESCE(SUM(total), 0)
            FROM fact_bill
            WHERE company_key = ?
              AND date_key BETWEEN ? AND ?
            """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
    }

    public BigDecimal getCashBalance(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
            SELECT COALESCE(SUM(f.close_balance_debit - f.close_balance_credit), 0)
            FROM fact_chart_balance_snapshot f
            JOIN dim_chart_account a
                ON f.chart_account_key = a.chart_key
            WHERE f.company_key = ?
              AND f.date_key BETWEEN ? AND ?
              AND a.is_current = true
              AND LOWER(a.account_type) IN (
                    'cash and cash equivalents',
                    'bank balance'
              )
            """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
    }

    public BigDecimal getBankAccountBalance(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
            SELECT COALESCE(SUM(f.close_balance_debit - f.close_balance_credit), 0)
            FROM fact_chart_balance_snapshot f
            JOIN dim_chart_account a
                ON f.chart_account_key = a.chart_key
            WHERE f.company_key = ?
              AND f.date_key BETWEEN ? AND ?
              AND a.is_current = true
              AND LOWER(a.account_type) = 'bank balance'
            """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
    }

    public BigDecimal getTotalLiabilities(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
            SELECT COALESCE(SUM(f.close_balance_credit - f.close_balance_debit), 0)
            FROM fact_chart_balance_snapshot f
            JOIN dim_chart_account a
                ON f.chart_account_key = a.chart_key
            WHERE f.company_key = ?
              AND f.date_key BETWEEN ? AND ?
              AND a.is_current = true
              AND LOWER(a.account_type) IN (
                    'current liability',
                    'liability',
                    'accrued salaries',
                    'accrued',
                    'gosi allowance - employee'
              )
            """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
    }

    public BigDecimal getAccountsReceivable(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
            SELECT COALESCE(SUM(total - COALESCE(partial_paid_amount, 0)), 0)
            FROM fact_invoice
            WHERE company_key = ?
              AND date_key BETWEEN ? AND ?
            """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
    }

    public BigDecimal getAccountsPayable(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
            SELECT COALESCE(SUM(fb.total), 0)
            FROM fact_bill fb
            JOIN dim_invoice_status s
                ON fb.status_key = s.status_key
            WHERE fb.company_key = ?
              AND fb.date_key BETWEEN ? AND ?
              AND s.status_code IN (
                    'UNPAID',
                    'WAITING',
                    'ACCEPTED',
                    'PARTIALLY_PAID'
              )
            """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
    }

    public Integer getNumberOfOpenInvoices(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
            SELECT COUNT(fi.invoice_id)
            FROM fact_invoice fi
            JOIN dim_invoice_status s
                ON fi.status_key = s.status_key
            WHERE fi.company_key = ?
              AND fi.date_key BETWEEN ? AND ?
              AND s.status_code IN (
                    'UNPAID',
                    'WAITING',
                    'ACCEPTED',
                    'PARTIALLY_PAID'
              )
            """;

        return queryForInteger(sql, companyKey, startDateKey, endDateKey);
    }

    public Integer getDueInvoices(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
            SELECT COUNT(fi.invoice_id)
            FROM fact_invoice fi
            JOIN dim_invoice_status s
                ON fi.status_key = s.status_key
            JOIN dim_date d
                ON fi.due_date_key = d.date_key
            WHERE fi.company_key = ?
              AND fi.date_key BETWEEN ? AND ?
              AND s.status_code IN (
                    'UNPAID',
                    'WAITING',
                    'ACCEPTED',
                    'PARTIALLY_PAID'
              )
              AND d.full_date < CURRENT_DATE
            """;

        return queryForInteger(sql, companyKey, startDateKey, endDateKey);
    }

    public BigDecimal getAssetValue(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
            SELECT COALESCE(SUM(asset_value), 0)
            FROM fact_asset
            WHERE company_key = ?
              AND date_key BETWEEN ? AND ?
            """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
    }

    public BigDecimal getDepreciationExpense(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
            SELECT COALESCE(SUM(depreciation_amount), 0)
            FROM fact_asset
            WHERE company_key = ?
              AND date_key BETWEEN ? AND ?
            """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
    }

    public BigDecimal getVatCollected(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
            SELECT COALESCE(SUM(fi.tax), 0)
            FROM fact_invoice fi
            JOIN dim_invoice_status s
                ON fi.status_key = s.status_key
            WHERE fi.company_key = ?
              AND fi.date_key BETWEEN ? AND ?
              AND s.status_code = 'PAID'
            """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
    }
    public BigDecimal getCurrentLiabilities(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
        SELECT COALESCE(SUM(f.close_balance_credit - f.close_balance_debit), 0)
        FROM fact_chart_balance_snapshot f
        JOIN dim_chart_account a
            ON f.chart_account_key = a.chart_key
        WHERE f.company_key = ?
          AND f.date_key BETWEEN ? AND ?
          AND a.is_current = true
          AND LOWER(a.account_type) IN (
                'current liability',
                'accrued salaries',
                'accrued'
          )
        """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
    }
    public BigDecimal getVatFromBills(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
            SELECT COALESCE(SUM(tax), 0)
            FROM fact_bill
            WHERE company_key = ?
              AND date_key BETWEEN ? AND ?
            """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
    }

    public List<FinanceRevenueProfitTrendItem> getRevenueProfitTrend(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey
    ) {
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
               AND fi.company_key = ?
            WHERE d.date_key BETWEEN ? AND ?
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
               AND fb.company_key = ?
            WHERE d.date_key BETWEEN ? AND ?
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
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new FinanceRevenueProfitTrendItem(
                        rs.getString("period"),
                        rs.getBigDecimal("revenue"),
                        rs.getBigDecimal("profit")
                ),
                companyKey,
                startDateKey,
                endDateKey,
                companyKey,
                startDateKey,
                endDateKey
        );
    }
    public List<FinanceCashFlowTrendItem> getCashFlowTrend(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey
    ) {
        String sql = """
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
        WHERE f.company_key = ?
          AND f.date_key BETWEEN ? AND ?
          AND a.is_current = true
          AND LOWER(a.account_type) IN (
                'cash and cash equivalents',
                'bank balance'
          )
        GROUP BY d.full_date
        ORDER BY d.full_date
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new FinanceCashFlowTrendItem(
                        rs.getString("period"),
                        rs.getBigDecimal("inflow"),
                        rs.getBigDecimal("outflow"),
                        rs.getBigDecimal("net_cash_flow")
                ),
                companyKey,
                startDateKey,
                endDateKey
        );
    }
    public List<FinanceOutstandingInvoiceItem> getTopOutstandingInvoices(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey
    ) {
        String sql = """
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
        WHERE fi.company_key = ?
          AND fi.date_key BETWEEN ? AND ?
          AND s.status_code IN (
                'UNPAID',
                'WAITING',
                'ACCEPTED',
                'PARTIALLY_PAID'
          )
          AND COALESCE(fi.total - COALESCE(fi.partial_paid_amount, 0), 0) > 0
        ORDER BY amount DESC
        LIMIT 5
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new FinanceOutstandingInvoiceItem(
                        rs.getString("client"),
                        rs.getString("reference"),
                        rs.getBigDecimal("amount"),
                        rs.getDate("due_date") != null ? rs.getDate("due_date").toLocalDate() : null,
                        rs.getString("status")
                ),
                companyKey,
                startDateKey,
                endDateKey
        );
    }
    public FinanceLiabilityAssetItem getLiabilityVsAssets(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey
    ) {
        String sql = """
        
                WITH assets AS (
            SELECT
                0 AS current_assets,
                COALESCE(SUM(f.asset_value), 0) AS fixed_assets,
                COALESCE(SUM(f.asset_value), 0) AS total_assets
            FROM fact_asset f
            WHERE f.company_key = ?
              AND f.date_key <= ?
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
            WHERE cb.company_key = ?
              AND cb.date_key BETWEEN ? AND ?
              AND a.is_current = true
        )
        SELECT
            assets.current_assets,
            assets.fixed_assets,
            assets.total_assets,
            liabilities.current_liabilities,
            liabilities.long_term_liabilities,
            liabilities.total_liabilities
        FROM assets, liabilities
        """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> new FinanceLiabilityAssetItem(
                        rs.getBigDecimal("current_assets"),
                        rs.getBigDecimal("fixed_assets"),
                        rs.getBigDecimal("total_assets"),
                        rs.getBigDecimal("current_liabilities"),
                        rs.getBigDecimal("long_term_liabilities"),
                        rs.getBigDecimal("total_liabilities")
                ),
                companyKey,
                endDateKey,
                companyKey,
                startDateKey,
                endDateKey
        );
    }
    public List<FinanceAssetDistributionItem> getAssetDistribution(
            Integer companyKey,
            Integer endDateKey
    ) {
        String sql = """
        SELECT
            COALESCE(t.asset_type, 'Unknown Asset Type') AS asset_type,
            COALESCE(SUM(f.asset_value), 0) AS asset_value
        FROM fact_asset f
        LEFT JOIN dim_asset_type t
            ON f.asset_type_key = t.asset_type_key
        WHERE f.company_key = ?
          AND f.date_key <= ?
        GROUP BY COALESCE(t.asset_type, 'Unknown Asset Type')
        ORDER BY asset_value DESC
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new FinanceAssetDistributionItem(
                        rs.getString("asset_type"),
                        rs.getBigDecimal("asset_value")
                ),
                companyKey,
                endDateKey
        );
    }



    public List<FinanceTaxPaymentItem> getRecentTaxPayments(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey
    ) {
        String sql = """
        SELECT
            'TAX' AS code,
            COALESCE(v.vendor_name, 'Tax Payment') AS label,
            COALESCE(SUM(fb.tax), 0) AS amount
        FROM fact_bill fb
        LEFT JOIN dim_vendor v
            ON fb.vendor_key = v.vendor_key
        WHERE fb.company_key = ?
          AND fb.date_key BETWEEN ? AND ?
          AND COALESCE(fb.tax, 0) > 0
        GROUP BY COALESCE(v.vendor_name, 'Tax Payment')
        ORDER BY amount DESC
        LIMIT 5
    """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new FinanceTaxPaymentItem(
                        rs.getString("code"),
                        rs.getString("label"),
                        rs.getBigDecimal("amount")
                ),
                companyKey,
                startDateKey,
                endDateKey
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
}