package model;

import java.math.BigDecimal;

/**
 * Du lieu membership tinh dong de hien thi tren trang profile.
 * Khong duoc luu vao bang Accounts.
 */
public class MembershipSummary {

    private final String tier;
    private final BigDecimal currentMonthSpent;

    public MembershipSummary(String tier, BigDecimal currentMonthSpent) {
        this.tier = tier;
        this.currentMonthSpent = currentMonthSpent;
    }

    public String getTier() {
        return tier;
    }

    public BigDecimal getCurrentMonthSpent() {
        return currentMonthSpent;
    }
}