package com.rest_erp.backend_bi_rest_erp.dto.hr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeesByDepartmentItem {
    private String department;
    private Long count;
}