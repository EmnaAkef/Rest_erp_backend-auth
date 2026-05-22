package com.rest_erp.backend_bi_rest_erp.bi.dto.sales;

import lombok.Data;

@Data
public class SalesFilterRequest {
//    private Integer customerKey;
//    private Integer productKey;
//    private Integer salespersonKey;
//    private Integer workstatusKey;
//
//    private String customerCategory;
//    private String productCategory;
//
//    private String salesOrderStatus;
//    private String invoiceStatus;
//    private String quotationStatus;
//    private String paymentState;

    private Long customerId;
    private Integer productKey;
    private Integer salespersonKey;
    private String workstatusLabel;
    private String customerCategory;
    private String customerName;
    private String productCategory;
}