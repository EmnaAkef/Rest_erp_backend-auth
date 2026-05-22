package com.rest_erp.backend_bi_rest_erp.company.dto;

public class CompanyOptionResponse {

    private Long companyId;
    private Integer companyKey;
    private String companyName;

    public CompanyOptionResponse() {
    }

    public CompanyOptionResponse(Long companyId, Integer companyKey, String companyName) {
        this.companyId = companyId;
        this.companyKey = companyKey;
        this.companyName = companyName;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Integer getCompanyKey() {
        return companyKey;
    }

    public void setCompanyKey(Integer companyKey) {
        this.companyKey = companyKey;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}