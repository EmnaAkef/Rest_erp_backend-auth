package com.rest_erp.backend_bi_rest_erp.dto.finance;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FinanceOutstandingInvoiceItem {

    private String client;
    private String reference;
    private BigDecimal amount;
    private LocalDate dueDate;
    private String status;

    public FinanceOutstandingInvoiceItem() {
    }

    public FinanceOutstandingInvoiceItem(String client, String reference, BigDecimal amount, LocalDate dueDate, String status) {
        this.client = client;
        this.reference = reference;
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}