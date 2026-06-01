package com.rest_erp.backend_bi_rest_erp.bi.dto.finance;

import java.util.List;

public record FinanceFilterOptionsResponse(
        List<String> customerNames,
        List<String> customerCategories,
        List<String> vendorNames,
        List<String> vendorIndustries,
        List<String> accountNames,

        List<String> invoiceStatuses,
        List<String> statusGroups,
        List<String> accountTypes,
        List<String> transactionTypes,
        List<String> assetTypes
) {}