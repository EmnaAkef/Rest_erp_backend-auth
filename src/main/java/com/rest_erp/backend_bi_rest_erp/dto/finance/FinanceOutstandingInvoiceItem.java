package com.rest_erp.backend_bi_rest_erp.dto.finance;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FinanceOutstandingInvoiceItem {

    private String client;
    private String reference;
    private BigDecimal amount;
    private LocalDate dueDate;
    private String status;



}