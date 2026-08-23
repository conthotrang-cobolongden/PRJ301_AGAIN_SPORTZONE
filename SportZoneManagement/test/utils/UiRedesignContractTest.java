package utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class UiRedesignContractTest {

    private UiRedesignContractTest() { }

    public static void main(String[] args) throws Exception {
        authContracts();
        accountContracts();
        bookingContracts();
        adminContracts();
        sharedContracts();
        System.out.println("UiRedesignContractTest PASSED");
    }

    private static void authContracts() throws Exception {
        requireAll("web/WEB-INF/views/auth/login.jsp",
                "action=login", "method=\"post\"", "name=\"csrfToken\"",
                "name=\"email\"", "name=\"password\"", "sz-page-auth");
        requireAll("web/WEB-INF/views/auth/register.jsp",
                "action=register", "method=\"post\"", "name=\"csrfToken\"",
                "name=\"fullName\"", "name=\"email\"", "name=\"phone\"",
                "name=\"password\"", "name=\"confirmPassword\"",
                "id=\"registerForm\"", "sz-page-auth");
    }

    private static void accountContracts() throws Exception {
        requireAll("web/WEB-INF/views/account/profile.jsp",
                "action=profile", "name=\"csrfToken\"", "name=\"fullName\"",
                "name=\"email\"", "name=\"phone\"", "sessionScope.frozen",
                "membershipSummary", "sz-page-account", "Coming Soon");
        requireAll("web/WEB-INF/views/account/password.jsp",
                "action=password", "name=\"csrfToken\"",
                "name=\"currentPassword\"", "name=\"newPassword\"",
                "name=\"confirmPassword\"", "id=\"szPasswordForm\"",
                "sz-page-account");
    }

    private static void bookingContracts() throws Exception {
        requireAll("web/WEB-INF/views/booking/create.jsp",
                "action=bookingCreate", "name=\"csrfToken\"",
                "name=\"courtId\"", "name=\"date\"", "name=\"slotId\"",
                "name=\"serviceId\"", "name=\"qty_${sv.serviceId}\"",
                "name=\"notes\"", "id=\"szBookingForm\"", "id=\"szSumTotal\"",
                "data-court-price", "data-start", "data-end", "sz-page-booking");
        requireAll("web/WEB-INF/views/booking/list.jsp",
                "upcomingList", "historyList", "activeTab", "action=bookingDetail",
                "sz-page-booking");
        requireAll("web/WEB-INF/views/booking/detail.jsp",
                "serviceLines", "paymentStatus", "action=invoicePayment",
                "action=invoiceDetail", "sz-page-booking");
        requireAll("web/WEB-INF/views/invoice/payment.jsp",
                "action=invoicePayment", "name=\"csrfToken\"",
                "name=\"bookingId\"", "name=\"operation\"", "value=\"pay\"",
                "value=\"cancel\"", "sz-page-invoice");
    }

    private static void adminContracts() throws Exception {
        requireAll("web/WEB-INF/views/admin/dashboard.jsp",
                "dashboard.totalAccounts", "dashboard.totalCourts",
                "dashboard.totalBookings", "dashboard.totalRevenue",
                "Coming Soon", "sz-page-admin");
        requireAll("web/WEB-INF/views/admin/courts.jsp",
                "action=adminCourts", "name=\"csrfToken\"",
                "value=\"generateWeek\"", "name=\"weekStart\"",
                "value=\"updateCourt\"", "name=\"courtId\"",
                "name=\"price\"", "name=\"status\"", "sz-page-admin");
        requireAll("web/WEB-INF/views/admin/bookings.jsp",
                "action=adminBookings", "name=\"status\"", "checkIn",
                "canCheckIn", "sessionScope.account.accountID",
                "Khách vãng lai", "sz-page-admin");
    }

    private static void sharedContracts() throws Exception {
        requireAll("web/WEB-INF/views/layout/header.jsp",
                "action=home", "action=courts", "action=adminDashboard",
                "action=adminCourts", "action=adminBookings", "action=bookings",
                "action=invoices", "action=profile", "action=logout",
                "action=login", "action=register", "sessionScope.roleName",
                "sessionScope.frozen", "pageCss");
        String views = read("web/WEB-INF/views");
        require(!views.contains("inspector-toggle")
                && !views.contains("demo-switcher")
                && !views.contains("quick-demo"),
                "Production JSPs must not contain demo-only controls");
    }

    private static void requireAll(String path, String... values)
            throws Exception {
        String source = read(path);
        for (String value : values) {
            require(source.contains(value), path + " missing contract: " + value);
        }
    }

    private static String read(String path) throws Exception {
        java.nio.file.Path target = Paths.get(path);
        if (Files.isDirectory(target)) {
            StringBuilder result = new StringBuilder();
            Files.walk(target).filter(Files::isRegularFile).forEach(file -> {
                try {
                    result.append(new String(Files.readAllBytes(file),
                            StandardCharsets.UTF_8));
                } catch (java.io.IOException exception) {
                    throw new RuntimeException(exception);
                }
            });
            return result.toString();
        }
        return new String(Files.readAllBytes(target), StandardCharsets.UTF_8);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
