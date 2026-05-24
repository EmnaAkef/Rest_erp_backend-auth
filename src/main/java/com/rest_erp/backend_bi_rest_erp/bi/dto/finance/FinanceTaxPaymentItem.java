package com.rest_erp.backend_bi_rest_erp.bi.dto.finance;

import java.math.BigDecimal;

public record FinanceTaxPaymentItem(
        String code,
        String label,
        BigDecimal amount
) {}