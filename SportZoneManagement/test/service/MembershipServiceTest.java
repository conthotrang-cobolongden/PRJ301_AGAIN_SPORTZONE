package service;

import dao.InvoiceDAO;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import model.MembershipSummary;

/**
 * Business test day du cho membership tinh dong.
 */
public class MembershipServiceTest {

    private static final LocalDate REFERENCE_DATE =
            LocalDate.of(2026, 8, 23);

    public static void main(String[] args) throws Exception {
        shouldClassifyAllCurrentMonthThresholds();
        shouldKeepHigherTierEarnedFromPreviousMonth();
        shouldUpgradeImmediatelyWhenCurrentMonthReachesHigherTier();
        shouldExposeCurrentMonthSpent();
        shouldReadPreviousAndCurrentCalendarMonths();

        System.out.println("MembershipServiceTest PASSED");
    }

    private static void shouldClassifyAllCurrentMonthThresholds()
            throws Exception {
        assertTier("MEMBER", "0");
        assertTier("MEMBER", "999999");

        assertTier("SILVER", "1000000");
        assertTier("SILVER", "1999999");

        assertTier("PLATINUM", "2000000");
        assertTier("PLATINUM", "4999999");

        assertTier("VIP", "5000000");
        assertTier("VIP", "9000000");
    }

    private static void shouldKeepHigherTierEarnedFromPreviousMonth()
            throws Exception {
        assertTier("VIP", "5000000", "0");
        assertTier("PLATINUM", "2000000", "1000000");
        assertTier("SILVER", "1000000", "0");
    }

    private static void shouldUpgradeImmediatelyWhenCurrentMonthReachesHigherTier()
            throws Exception {
        assertTier("PLATINUM", "1000000", "2000000");
        assertTier("VIP", "2000000", "5000000");
    }

    private static void shouldExposeCurrentMonthSpent()
            throws Exception {
        FakeInvoiceDAO invoiceDAO = new FakeInvoiceDAO(
                new BigDecimal("5000000"),
                new BigDecimal("1500000")
        );

        MembershipSummary summary =
                new MembershipService(invoiceDAO).getSummary(
                        2,
                        REFERENCE_DATE
                );

        assertMoney(new BigDecimal("1500000"),
                summary.getCurrentMonthSpent(),
                "Profile phai hien thi chi tieu thang hien tai");
    }

    private static void shouldReadPreviousAndCurrentCalendarMonths()
            throws Exception {
        FakeInvoiceDAO invoiceDAO = new FakeInvoiceDAO(
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        new MembershipService(invoiceDAO).getSummary(
                2,
                REFERENCE_DATE
        );

        assertEquals(2, invoiceDAO.callCount,
                "Phai doc dung hai ky chi tieu");

        assertEquals(LocalDate.of(2026, 7, 1),
                invoiceDAO.previousFrom,
                "Thang truoc phai bat dau 01/07/2026");

        assertEquals(LocalDate.of(2026, 8, 1),
                invoiceDAO.previousTo,
                "Thang truoc phai ket thuc truoc 01/08/2026");

        assertEquals(LocalDate.of(2026, 8, 1),
                invoiceDAO.currentFrom,
                "Thang hien tai phai bat dau 01/08/2026");

        assertEquals(LocalDate.of(2026, 9, 1),
                invoiceDAO.currentTo,
                "Thang hien tai phai ket thuc truoc 01/09/2026");
    }

    private static void assertTier(String expectedTier,
            String currentMonthSpent) throws Exception {
        assertTier(expectedTier, "0", currentMonthSpent);
    }

    private static void assertTier(String expectedTier,
            String previousMonthSpent,
            String currentMonthSpent) throws Exception {
        FakeInvoiceDAO invoiceDAO = new FakeInvoiceDAO(
                new BigDecimal(previousMonthSpent),
                new BigDecimal(currentMonthSpent)
        );

        MembershipSummary summary =
                new MembershipService(invoiceDAO).getSummary(
                        2,
                        REFERENCE_DATE
                );

        assertEquals(expectedTier, summary.getTier(),
                "Sai membership tier voi previous="
                + previousMonthSpent
                + ", current="
                + currentMonthSpent);
    }

    private static void assertEquals(Object expected, Object actual,
            String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(
                    message + " | expected=" + expected
                    + ", actual=" + actual
            );
        }
    }

    private static void assertMoney(BigDecimal expected, BigDecimal actual,
            String message) {
        if (actual == null || expected.compareTo(actual) != 0) {
            throw new AssertionError(
                    message + " | expected=" + expected
                    + ", actual=" + actual
            );
        }
    }

    private static final class FakeInvoiceDAO extends InvoiceDAO {

        private final BigDecimal previousMonthSpent;
        private final BigDecimal currentMonthSpent;

        private int callCount;
        private LocalDate previousFrom;
        private LocalDate previousTo;
        private LocalDate currentFrom;
        private LocalDate currentTo;

        private FakeInvoiceDAO(BigDecimal previousMonthSpent,
                BigDecimal currentMonthSpent) {
            this.previousMonthSpent = previousMonthSpent;
            this.currentMonthSpent = currentMonthSpent;
        }

        @Override
        public BigDecimal sumPaidByAccountAndPeriod(
                int accountID,
                LocalDate fromInclusive,
                LocalDate toExclusive
        ) throws SQLException {
            if (accountID != 2) {
                throw new AssertionError("Sai AccountID: " + accountID);
            }

            callCount++;

            if (LocalDate.of(2026, 7, 1).equals(fromInclusive)
                    && LocalDate.of(2026, 8, 1).equals(toExclusive)) {
                previousFrom = fromInclusive;
                previousTo = toExclusive;
                return previousMonthSpent;
            }

            if (LocalDate.of(2026, 8, 1).equals(fromInclusive)
                    && LocalDate.of(2026, 9, 1).equals(toExclusive)) {
                currentFrom = fromInclusive;
                currentTo = toExclusive;
                return currentMonthSpent;
            }

            throw new AssertionError(
                    "Sai khoang thoi gian: "
                    + fromInclusive + " -> " + toExclusive
            );
        }
    }
}