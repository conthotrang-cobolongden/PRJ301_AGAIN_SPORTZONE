package model;

import java.math.BigDecimal;

public class AdminDashboard {
    private final int totalAccounts;
    private final int totalCourts;
    private final int totalBookings;
    private final BigDecimal totalRevenue;

    public AdminDashboard(int totalAccounts, int totalCourts,
            int totalBookings, BigDecimal totalRevenue) {
        this.totalAccounts = totalAccounts;
        this.totalCourts = totalCourts;
        this.totalBookings = totalBookings;
        this.totalRevenue = totalRevenue;
    }

    public int getTotalAccounts() { return totalAccounts; }
    public int getTotalCourts() { return totalCourts; }
    public int getTotalBookings() { return totalBookings; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
}
