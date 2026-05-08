package com.rest_erp.backend_bi_rest_erp.repository.overview;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewFinancialTrendItem;
import java.util.List;
import java.math.BigDecimal;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewFinancialTrendItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewCashSummaryItem;
@Repository
public class OverviewKpiRepository {

    private final JdbcTemplate jdbcTemplate;

    public OverviewKpiRepository(JdbcTemplate jdbcTemplate) {
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

    public Integer getTotalEmployees(Integer companyKey) {
        String sql = """
            SELECT COUNT(*)
            FROM dim_user
            WHERE company_key = ?
              AND is_current = true
              AND COALESCE(active, true) = true
            """;

        return queryForInteger(sql, companyKey);
    }

    public BigDecimal getPresenceRate(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
        SELECT 
            CASE
                WHEN COALESCE(SUM(scheduled_shift_count), 0) = 0 THEN 0
                ELSE ROUND(
                    (
                        COALESCE(SUM(present_shift_count), 0) * 100.0
                    ) / COALESCE(SUM(scheduled_shift_count), 0),
                    2
                )
            END
        FROM fact_attendance_shift
        WHERE company_key = ?
          AND date_key BETWEEN ? AND ?
        """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
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

    public BigDecimal getWinRate(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
            SELECT
                CASE
                    WHEN COUNT(CASE WHEN f.close_date_key IS NOT NULL THEN 1 END) = 0 THEN 0
                    ELSE ROUND(
                        (
                            COUNT(
                                CASE
                                    WHEN f.close_date_key IS NOT NULL
                                     AND LOWER(ws.status_label) IN ('win', 'done')
                                    THEN 1
                                END
                            ) * 100.0
                        ) / COUNT(CASE WHEN f.close_date_key IS NOT NULL THEN 1 END),
                        2
                    )
                END
            FROM fact_deal f
            LEFT JOIN dim_workstatus ws
                ON ws.workstatus_key = f.workstatus_key
            LEFT JOIN dim_date d
                ON d.date_key = f.close_date_key
            WHERE f.company_key = ?
              AND d.full_date >= TO_DATE(CAST(? AS TEXT), 'YYYYMMDD')
              AND d.full_date <= TO_DATE(CAST(? AS TEXT), 'YYYYMMDD')
            """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
    }

    public BigDecimal getPipelineValue(Integer companyKey) {
        String sql = """
            SELECT COALESCE(SUM(f.deal_value), 0)
            FROM fact_deal f
            LEFT JOIN dim_workstatus ws
                ON ws.workstatus_key = f.workstatus_key
            WHERE f.company_key = ?
              AND COALESCE(f.is_archived, false) = false
              AND LOWER(COALESCE(ws.status_label, '')) NOT IN ('win', 'done', 'lost', 'closed')
            """;

        return queryForBigDecimal(sql, companyKey);
    }

    public List<OverviewFinancialTrendItem> getFinancialTrend(
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
            r.revenue,
            e.expenses,
            r.revenue - e.expenses AS net_profit
        FROM revenue_by_month r
        JOIN expenses_by_month e
            ON r.year = e.year
           AND r.month = e.month
        ORDER BY r.year, r.month
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new OverviewFinancialTrendItem(
                        rs.getString("period"),
                        rs.getBigDecimal("revenue"),
                        rs.getBigDecimal("expenses"),
                        rs.getBigDecimal("net_profit")
                ),
                companyKey,
                startDateKey,
                endDateKey,
                companyKey,
                startDateKey,
                endDateKey
        );
    }
    public OverviewCashSummaryItem getCashSummary(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey
    ) {
        String sql = """
        WITH cash_balance AS (
            SELECT
                COALESCE(SUM(f.close_balance_debit - f.close_balance_credit), 0) AS cash_balance
            FROM fact_chart_balance_snapshot f
            JOIN dim_chart_account a
                ON f.chart_account_key = a.chart_key
            WHERE f.company_key = ?
              AND f.date_key <= ?
              AND a.is_current = true
              AND LOWER(a.account_type) IN (
                    'cash and cash equivalents',
                    'bank balance'
              )
        ),
        cash_flow AS (
            SELECT
                COALESCE(SUM(f.debit), 0) AS inflow,
                COALESCE(SUM(f.credit), 0) AS outflow
            FROM fact_cash_movement f
            JOIN dim_chart_account a
                ON f.chart_account_key = a.chart_key
            WHERE f.company_key = ?
              AND f.date_key BETWEEN ? AND ?
              AND a.is_current = true
              AND LOWER(a.account_type) IN (
                    'cash and cash equivalents',
                    'bank balance'
              )
        )
        SELECT
            cash_balance.cash_balance,
            cash_flow.inflow,
            cash_flow.outflow
        FROM cash_balance, cash_flow
        """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> new OverviewCashSummaryItem(
                        rs.getBigDecimal("cash_balance"),
                        rs.getBigDecimal("inflow"),
                        rs.getBigDecimal("outflow")
                ),
                companyKey,
                endDateKey,
                companyKey,
                startDateKey,
                endDateKey
        );
    }

}