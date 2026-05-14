package com.rest_erp.backend_bi_rest_erp.dto.hr;

import lombok.Data;

@Data
public class HrFilterRequest {

    private String departmentName;
    private String employeeName;

    private String gender;
    private String position;
    private String employeeType;

    private Boolean active;

    private String workstatusLabel;
}