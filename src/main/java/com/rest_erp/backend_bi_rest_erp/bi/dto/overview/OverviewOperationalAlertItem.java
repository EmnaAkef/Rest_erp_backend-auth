package com.rest_erp.backend_bi_rest_erp.bi.dto.overview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverviewOperationalAlertItem {

    private String category;
    private String status;
    private String title;
    private BigDecimal value;
    private String valueSuffix;
    private String color;
}