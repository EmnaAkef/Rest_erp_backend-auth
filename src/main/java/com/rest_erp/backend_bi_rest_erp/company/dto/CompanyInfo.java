package com.rest_erp.backend_bi_rest_erp.company.dto;

public class CompanyInfo {

    private Long companyId;
    private Integer companyKey;
    private String companyName;

    public CompanyInfo() {
    }

    public CompanyInfo(Long companyId, Integer companyKey, String companyName) {
        this.companyId = companyId;
        this.companyKey = companyKey;
        this.companyName = companyName;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public Integer getCompanyKey() {
        return companyKey;
    }

    public String getCompanyName() {
        return companyName;
    }
}