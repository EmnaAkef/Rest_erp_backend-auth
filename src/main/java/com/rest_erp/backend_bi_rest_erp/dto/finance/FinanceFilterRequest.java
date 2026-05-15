package com.rest_erp.backend_bi_rest_erp.dto.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceFilterRequest {

    // Customer / invoice filters
    private String customerName;
    private String customerCategory;

    // Vendor / bill filters
    private String vendorName;
    private String vendorIndustry;

    // Status filters
    private String invoiceStatus;
    private String statusGroup;

    // Chart account filters
    private String accountName;
    private String accountType;
    private String transactionType;

    // Asset filters
    private String assetType;

    // Amount filters
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
}