package com.rest_erp.backend_bi_rest_erp.bi.controller.hr;

import com.rest_erp.backend_bi_rest_erp.bi.dto.hr.*;
import com.rest_erp.backend_bi_rest_erp.bi.service.hr.HrKpiService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bi/hr")
@RequiredArgsConstructor
public class HrKpiController {

    private final HrKpiService hrKpiService;

    private HrFilterRequest buildHrFilters(
            String departmentName,
            String employeeName,
            String gender,
            String position,
            String employeeType,
            Boolean active,
            String workstatusLabel
    ) {
        HrFilterRequest filters = new HrFilterRequest();

        filters.setDepartmentName(departmentName);
        filters.setEmployeeName(employeeName);
        filters.setGender(gender);
        filters.setPosition(position);
        filters.setEmployeeType(employeeType);
        filters.setActive(active);
        filters.setWorkstatusLabel(workstatusLabel);

        return filters;
    }

    @GetMapping("/kpis")
    public HrKpiResponse getHrKpis(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String employeeType,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String workstatusLabel
    ) {
        HrFilterRequest filters = buildHrFilters(
                departmentName,
                employeeName,
                gender,
                position,
                employeeType,
                active,
                workstatusLabel
        );

        return hrKpiService.getHrKpis(startDate, endDate, filters);
    }

    @GetMapping("/headcount-trend")
    public ResponseEntity<List<Map<String, Object>>> getHeadcountTrend(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String employeeType,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String workstatusLabel
    ) {
        HrFilterRequest filters = buildHrFilters(
                departmentName,
                employeeName,
                gender,
                position,
                employeeType,
                active,
                workstatusLabel
        );

        return ResponseEntity.ok(
                hrKpiService.getHeadcountTrend(startDate, endDate, filters)
        );
    }

    @GetMapping("/attendance-trend")
    public List<AttendanceTrendItem> getAttendanceTrend(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String employeeType,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String workstatusLabel
    ) {
        HrFilterRequest filters = buildHrFilters(
                departmentName,
                employeeName,
                gender,
                position,
                employeeType,
                active,
                workstatusLabel
        );

        return hrKpiService.getAttendanceTrend(startDate, endDate, filters);
    }

    @GetMapping("/tenure-distribution")
    public List<TenureDistributionItem> getTenureDistribution(
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String employeeType,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String workstatusLabel
    ) {
        HrFilterRequest filters = buildHrFilters(
                departmentName,
                employeeName,
                gender,
                position,
                employeeType,
                active,
                workstatusLabel
        );

        return hrKpiService.getTenureDistribution(filters);
    }

    @GetMapping("/employees-by-department")
    public ResponseEntity<List<Map<String, Object>>> getEmployeesByDepartment(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String employeeType,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String workstatusLabel
    ) {
        HrFilterRequest filters = buildHrFilters(
                departmentName,
                employeeName,
                gender,
                position,
                employeeType,
                active,
                workstatusLabel
        );

        return ResponseEntity.ok(
                hrKpiService.getEmployeesByDepartment(startDate, endDate, filters)
        );
    }

    @GetMapping("/salary-benchmarking")
    public List<SalaryBenchmarkItem> getSalaryBenchmarking(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String employeeType,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String workstatusLabel
    ) {
        HrFilterRequest filters = buildHrFilters(
                departmentName,
                employeeName,
                gender,
                position,
                employeeType,
                active,
                workstatusLabel
        );

        return hrKpiService.getSalaryBenchmarking(startDate, endDate, filters);
    }

    @GetMapping("/hiring-funnel")
    public List<HiringFunnelItem> getHiringFunnel(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String employeeType,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String workstatusLabel
    ) {
        HrFilterRequest filters = buildHrFilters(
                departmentName,
                employeeName,
                gender,
                position,
                employeeType,
                active,
                workstatusLabel
        );

        return hrKpiService.getHiringFunnel(startDate, endDate, filters);
    }

    @GetMapping("/upcoming-birthdays")
    public List<UpcomingBirthdayItem> getUpcomingBirthdays(
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String employeeType,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String workstatusLabel
    ) {
        HrFilterRequest filters = buildHrFilters(
                departmentName,
                employeeName,
                gender,
                position,
                employeeType,
                active,
                workstatusLabel
        );

        return hrKpiService.getUpcomingBirthdays(filters);
    }

    @GetMapping("/filter-options/departments")
    public List<HrFilterOption> getDepartmentOptions() {
        return hrKpiService.getDepartmentOptions();
    }

    @GetMapping("/filter-options/employees")
    public List<HrFilterOption> getEmployeeOptions() {
        return hrKpiService.getEmployeeOptions();
    }

    @GetMapping("/filter-options/genders")
    public List<HrFilterOption> getGenderOptions() {
        return hrKpiService.getGenderOptions();
    }

    @GetMapping("/filter-options/positions")
    public List<HrFilterOption> getPositionOptions() {
        return hrKpiService.getPositionOptions();
    }

    @GetMapping("/filter-options/employee-types")
    public List<HrFilterOption> getEmployeeTypeOptions() {
        return hrKpiService.getEmployeeTypeOptions();
    }

    @GetMapping("/filter-options/workstatus")
    public List<HrFilterOption> getWorkstatusOptions() {
        return hrKpiService.getWorkstatusOptions();
    }
}