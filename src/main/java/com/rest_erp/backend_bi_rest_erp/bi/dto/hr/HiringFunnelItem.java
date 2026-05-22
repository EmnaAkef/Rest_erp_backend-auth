package com.rest_erp.backend_bi_rest_erp.bi.dto.hr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HiringFunnelItem {
    private String stage;
    private Long count;
}