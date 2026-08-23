package utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class AdminMvpWebContractTest {

    private AdminMvpWebContractTest() { }

    public static void main(String[] args) throws Exception {
        String main = read("src/java/controller/MainController.java");
        String auth = read("src/java/filter/AuthFilter.java");
        String xml = read("web/WEB-INF/web.xml");
        String dashboardController = read(
                "src/java/controller/AdminDashboardServlet.java");
        String courtsController = read(
                "src/java/controller/AdminCourtsServlet.java");
        String bookingsController = read(
                "src/java/controller/AdminBookingsServlet.java");
        String create = read("src/java/controller/BookingCreateServlet.java");
        String payment = read("src/java/controller/InvoicePaymentServlet.java");
        String invoiceDetail = read(
                "src/java/controller/InvoiceDetailServlet.java");
        String dashboard = read("web/WEB-INF/views/admin/dashboard.jsp");
        String courts = read("web/WEB-INF/views/admin/courts.jsp");
        String bookings = read("web/WEB-INF/views/admin/bookings.jsp");
        String header = read("web/WEB-INF/views/layout/header.jsp");
        String invoiceDao = read("src/java/dao/InvoiceDAO.java");

        require(main.contains("adminDashboard")
                && main.contains("adminCourts")
                && main.contains("adminBookings"),
                "MainController must route three Admin actions");
        require(auth.contains("adminDashboard")
                && auth.contains("adminCourts")
                && auth.contains("adminBookings"),
                "AuthFilter must protect Admin actions");
        require(xml.contains("AdminDashboardServlet")
                && xml.contains("AdminCourtsServlet")
                && xml.contains("AdminBookingsServlet")
                && !xml.contains("<url-pattern>/adminDashboard")
                && !xml.contains("<url-pattern>/adminCourts")
                && !xml.contains("<url-pattern>/adminBookings"),
                "Admin servlets must be named dispatch targets only");
        require(dashboardController.contains("SessionUtil.isAdmin")
                && courtsController.contains("SessionUtil.isAdmin")
                && bookingsController.contains("SessionUtil.isAdmin"),
                "Every Admin servlet must enforce role");
        require(courtsController.contains("CsrfUtil.isValid")
                && bookingsController.contains("CsrfUtil.isValid"),
                "Every Admin POST must validate CSRF");
        require(create.contains("ROLE_USER") && create.contains("ROLE_ADMIN"),
                "Booking creation must allow User and Admin");
        require(payment.contains("ROLE_USER") && payment.contains("ROLE_ADMIN")
                && invoiceDetail.contains("ROLE_USER")
                && invoiceDetail.contains("ROLE_ADMIN"),
                "Owned payment and invoice detail must allow User and Admin");
        require(dashboard.contains("totalAccounts")
                && dashboard.contains("totalCourts")
                && dashboard.contains("totalBookings")
                && dashboard.contains("totalRevenue"),
                "Dashboard must render four totals");
        require(courts.contains("updateCourt")
                && courts.contains("generateWeek"),
                "Court page must update court and generate week");
        require(bookings.contains("checkIn")
                && !bookings.contains("manualNoShow")
                && !bookings.contains("checkOut")
                && !bookings.contains("operation=cancel"),
                "Booking page must expose check-in only");
        require(header.contains("adminDashboard")
                && header.contains("adminCourts")
                && header.contains("adminBookings")
                && !header.contains("adminReports")
                && !header.contains("adminUsers")
                && !header.contains("adminServices"),
                "Header must contain only Admin MVP navigation");
        require(invoiceDao.contains("Khách vãng lai")
                && bookings.contains("Khách vãng lai"),
                "Admin booking and invoice must display walk-in name");
        require(bookings.contains("sessionScope.account.accountID")
                && !bookings.contains("sessionScope.account.accountId"),
                "Admin booking ownership must use Account JavaBean property");

        System.out.println("AdminMvpWebContractTest PASSED");
    }

    private static String read(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)),
                StandardCharsets.UTF_8);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
