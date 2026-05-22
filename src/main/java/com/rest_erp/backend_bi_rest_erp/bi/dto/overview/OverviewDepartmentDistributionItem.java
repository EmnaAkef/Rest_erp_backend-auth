package com.rest_erp.backend_bi_rest_erp.bi.dto.overview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverviewDepartmentDistributionItem {

    private String departmentName;
    private Long employeeCount;
}