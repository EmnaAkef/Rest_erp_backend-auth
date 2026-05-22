package com.rest_erp.backend_bi_rest_erp.bi.tenant;

import com.rest_erp.backend_bi_rest_erp.auth.entity.AppRole;

public class TenantContext {

    private static final ThreadLocal<Long> CURRENT_COMPANY_ID = new ThreadLocal<>();
    private static final ThreadLocal<Integer> CURRENT_COMPANY_KEY = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_USER_EMAIL = new ThreadLocal<>();
    private static final ThreadLocal<AppRole> CURRENT_USER_ROLE = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setCompanyId(Long companyId) {
        CURRENT_COMPANY_ID.set(companyId);
    }

    public static Long getCompanyId() {
        return CURRENT_COMPANY_ID.get();
    }

    public static void setCompanyKey(Integer companyKey) {
        CURRENT_COMPANY_KEY.set(companyKey);
    }

    public static Integer getCompanyKey() {
        return CURRENT_COMPANY_KEY.get();
    }

    public static void setUserEmail(String email) {
        CURRENT_USER_EMAIL.set(email);
    }

    public static String getUserEmail() {
        return CURRENT_USER_EMAIL.get();
    }

    public static void setUserRole(AppRole role) {
        CURRENT_USER_ROLE.set(role);
    }

    public static AppRole getUserRole() {
        return CURRENT_USER_ROLE.get();
    }

    public static boolean isSuperAdmin() {
        return CURRENT_USER_ROLE.get() == AppRole.SUPER_ADMIN;
    }

    public static boolean isCompanyAdmin() {
        return CURRENT_USER_ROLE.get() == AppRole.COMPANY_ADMIN;
    }

    public static boolean isUser() {
        return CURRENT_USER_ROLE.get() == AppRole.USER;
    }

    public static void clear() {
        CURRENT_COMPANY_ID.remove();
        CURRENT_COMPANY_KEY.remove();
        CURRENT_USER_EMAIL.remove();
        CURRENT_USER_ROLE.remove();
    }
}