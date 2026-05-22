package com.rest_erp.backend_bi_rest_erp.bi.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesFilterOption {
    private Object value;
    private String label;
}