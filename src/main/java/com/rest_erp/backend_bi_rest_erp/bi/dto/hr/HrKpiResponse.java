package com.rest_erp.backend_bi_rest_erp.bi.dto.hr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrKpiResponse {

    private String currency;

    private Long totalEmployees;
    private Long activeEmployees;
    private Long inactiveEmployees;

    private Long onboardingEmployees;
    private Long offboardingEmployees;

    private BigDecimal attritionRate;
    private BigDecimal averageTenure;

    private BigDecimal presenceRate;
    private BigDecimal absenceRate;
    private Long lateCheckins;
    private BigDecimal overtimeHours;

    private BigDecimal totalPayroll;
    private BigDecimal averageSalary;
    private BigDecimal averageCostPerEmployee;

    private Long activeJobOffers;
    private Long totalApplications;
    private BigDecimal conversionRate;
    private BigDecimal absenteeismVolatilityIndex;

    private Long hiredApplications;
    private BigDecimal lateArrivalPenalty;
    private String applicationQuality;
    private BigDecimal efficiencyIndex;
}