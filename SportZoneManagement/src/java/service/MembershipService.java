package service;

import dao.InvoiceDAO;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import model.MembershipSummary;

/**
 * Tinh membership tu tong chi tieu cua hoa don Paid.
 *
 * Hang hien tai la hang cao hon giua:
 * - Hang duy tri tu chi tieu thang truoc.
 * - Hang dat duoc trong thang hien tai.
 *
 * Khong luu tier hoac monthlySpent vao Accounts.
 */
public class MembershipService {

    private static final BigDecimal SILVER_MIN
            = new BigDecimal("1000000");

    private static final BigDecimal PLATINUM_MIN
            = new BigDecimal("2000000");

    private static final BigDecimal VIP_MIN
            = new BigDecimal("5000000");

    private final InvoiceDAO invoiceDAO;

    public MembershipService() {
        this(new InvoiceDAO());
    }

    public MembershipService(InvoiceDAO invoiceDAO) {
        if (invoiceDAO == null) {
            throw new IllegalArgumentException(
                    "InvoiceDAO must not be null"
            );
        }

        this.invoiceDAO = invoiceDAO;
    }

    public MembershipSummary getSummary(
            int accountID,
            LocalDate referenceDate
    ) throws SQLException {
        LocalDate currentMonthStart
                = referenceDate.withDayOfMonth(1);
        LocalDate previousMonthStart
                = currentMonthStart.minusMonths(1);
        LocalDate nextMonthStart
                = currentMonthStart.plusMonths(1);

        BigDecimal previousMonthSpent
                = invoiceDAO.sumPaidByAccountAndPeriod(
                        accountID,
                        previousMonthStart,
                        currentMonthStart
                );

        BigDecimal currentMonthSpent
                = invoiceDAO.sumPaidByAccountAndPeriod(
                        accountID,
                        currentMonthStart,
                        nextMonthStart
                );

        BigDecimal qualifyingSpent
                = previousMonthSpent.max(currentMonthSpent);

        return new MembershipSummary(
                determineTier(qualifyingSpent),
                currentMonthSpent
        );
    }

    private String determineTier(BigDecimal spent) {
        if (spent.compareTo(VIP_MIN) >= 0) {
            return "VIP";
        }

        if (spent.compareTo(PLATINUM_MIN) >= 0) {
            return "PLATINUM";
        }

        if (spent.compareTo(SILVER_MIN) >= 0) {
            return "SILVER";
        }

        return "MEMBER";
    }
}