package filter;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Account;
import model.AdminDashboard;
import model.Booking;
import model.Court;
import model.DateOption;
import model.Invoice;
import model.MembershipSummary;
import model.Service;
import model.TimeSlot;
import utils.CsrfUtil;
import utils.SessionUtil;
import utils.Views;

/** Serves the real JSP views with sample data, only when DEMO_MODE=true. */
public final class DemoFilter implements Filter {
    @Override public void init(FilterConfig config) { }
    @Override public void destroy() { }

    @Override
    public void doFilter(ServletRequest rawRequest, ServletResponse rawResponse,
            FilterChain chain) throws IOException, ServletException {
        if (!"true".equalsIgnoreCase(System.getenv("DEMO_MODE"))) {
            chain.doFilter(rawRequest, rawResponse);
            return;
        }

        HttpServletRequest request = (HttpServletRequest) rawRequest;
        HttpServletResponse response = (HttpServletResponse) rawResponse;
        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        HttpSession session = request.getSession();
        session.setAttribute("demoMode", Boolean.TRUE);
        CsrfUtil.ensureToken(session);
        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) action = "home";

        if ("logout".equals(action)) {
            session.invalidate();
            redirect(request, response, "home");
            return;
        }
        if ("login".equals(action) && "POST".equals(request.getMethod())) {
            if (!CsrfUtil.isValid(request)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            if ("demo".equals(password) && ("user@sportzone.demo".equalsIgnoreCase(email)
                    || "admin@sportzone.demo".equalsIgnoreCase(email))) {
                boolean admin = "admin@sportzone.demo".equalsIgnoreCase(email);
                session.invalidate();
                session = request.getSession(true);
                session.setAttribute("demoMode", Boolean.TRUE);
                SessionUtil.storeLoggedInAccount(session, sampleAccount(admin));
                redirect(request, response, admin ? "adminDashboard" : "home");
                return;
            }
            request.setAttribute("errorMessage", "Vui lòng dùng tài khoản demo được ghi bên dưới.");
        }

        if (requiresLogin(action) && !SessionUtil.isLoggedIn(session)) {
            redirect(request, response, "login");
            return;
        }
        if (action.startsWith("admin") && !SessionUtil.isAdmin(session)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Demo mutations are deliberately not persisted or reported as real changes.
        if ("POST".equals(request.getMethod()) && !"login".equals(action)) {
            if (!CsrfUtil.isValid(request)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            String notice = "Bản demo chỉ dùng để xem giao diện; thay đổi không được lưu.";
            request.setAttribute("demoNotice", notice);
            request.setAttribute("error", notice);
            request.setAttribute("errorMessage", notice);
        }

        List<Court> courts = sampleCourts();
        Court court = findCourt(courts, request.getParameter("id"), request.getParameter("courtId"));
        LocalDate date = selectedDate(request.getParameter("date"));
        List<TimeSlot> slots = sampleSlots(court, date);
        Booking paidBooking = sampleBooking();
        Booking unpaidBooking = sampleBooking();
        unpaidBooking.setBookingID(1002);
        unpaidBooking.setPaymentStatus("Unpaid");
        unpaidBooking.setStatus("Pending");
        String bookingId = request.getParameter("id");
        if (bookingId == null) bookingId = request.getParameter("bookingId");
        Booking booking = "1002".equals(bookingId) ? unpaidBooking : paidBooking;
        Invoice invoice = sampleInvoice();
        if ("invoicePayment".equals(action)) {
            invoice.setBookingID(booking.getBookingId());
            invoice.setPaymentStatus("Unpaid");
            invoice.setPaidAt(null);
        }
        request.setAttribute("tennisCourtCount", 5);
        request.setAttribute("pickleballCourtCount", 5);
        request.setAttribute("tennisCount", 5);
        request.setAttribute("pickleballCount", 5);
        String type = request.getParameter("type");
        if (!"tennis".equals(type) && !"pickleball".equals(type)) type = "all";
        List<Court> filtered = new ArrayList<Court>();
        for (Court item : courts) {
            if ("all".equals(type) || type.equals(item.getTypeSlug())) filtered.add(item);
        }
        request.setAttribute("typeFilter", type);
        request.setAttribute("courtList", "courts".equals(action) ? filtered : courts);
        request.setAttribute("court", court);
        request.setAttribute("slotList", slots);
        request.setAttribute("selectedDate", date.toString());
        request.setAttribute("selectedDateObj", java.sql.Date.valueOf(date));
        request.setAttribute("dateOptions", dateOptions(date));
        request.setAttribute("selectedCourtId", court.getCourtId());
        request.setAttribute("selectedCourtPrice", court.getPricePerHour());
        request.setAttribute("selectedSlotId", 0);
        request.setAttribute("minDate", LocalDate.now().toString());
        request.setAttribute("maxDate", LocalDate.now().plusDays(6).toString());
        request.setAttribute("serviceList", sampleServices());
        request.setAttribute("selectedServiceQuantities", Collections.emptyMap());
        request.setAttribute("booking", booking);
        request.setAttribute("upcomingList", Arrays.asList(paidBooking, unpaidBooking));
        request.setAttribute("historyList", Collections.emptyList());
        request.setAttribute("activeTab", "history".equals(request.getParameter("tab")) ? "history" : "upcoming");
        request.setAttribute("bookingList", Arrays.asList(paidBooking, unpaidBooking));
        request.setAttribute("serviceLines", Collections.emptyList());
        request.setAttribute("invoice", invoice);
        request.setAttribute("invoiceList", Collections.singletonList(invoice));
        request.setAttribute("dashboard", new AdminDashboard(128, 10, 256, new BigDecimal("48500000")));
        request.setAttribute("account", session.getAttribute("account"));
        request.setAttribute("membershipSummary", new MembershipSummary("Gold Member", new BigDecimal("1500000")));
        request.setAttribute("defaultWeek", LocalDate.now().plusWeeks(1).with(java.time.DayOfWeek.MONDAY).toString());

        String view = resolveView(action);
        if (view == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if ("adminBookings".equals(action) && request.getParameter("id") == null) {
            request.removeAttribute("booking");
        }
        request.getRequestDispatcher(view).forward(request, response);
    }

    private static void redirect(HttpServletRequest request, HttpServletResponse response, String action)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/MainController?action=" + action);
    }

    private static boolean requiresLogin(String action) {
        return Arrays.asList("profile", "password", "bookingCreate", "bookings", "bookingDetail",
                "accountLocked", "invoicePayment", "invoices", "invoiceDetail", "adminDashboard",
                "adminCourts", "adminBookings").contains(action);
    }

    private static String resolveView(String action) {
        switch (action) {
            case "home": return Views.HOME;
            case "login": return Views.LOGIN;
            case "register": return Views.REGISTER;
            case "courts": return Views.COURT_LIST;
            case "court": return Views.COURT_DETAIL;
            case "bookingCreate": return Views.BOOKING_CREATE;
            case "bookings": return Views.BOOKING_LIST;
            case "bookingDetail": return Views.BOOKING_DETAIL;
            case "invoicePayment": return Views.INVOICE_PAYMENT;
            case "invoices": return Views.INVOICE_LIST;
            case "invoiceDetail": return Views.INVOICE_DETAIL;
            case "profile": return Views.PROFILE;
            case "password": return Views.PASSWORD;
            case "accountLocked": return Views.ACCOUNT_LOCKED;
            case "adminDashboard": return Views.ADMIN_DASHBOARD;
            case "adminCourts": return Views.ADMIN_COURTS;
            case "adminBookings": return Views.ADMIN_BOOKINGS;
            default: return null;
        }
    }

    private static Account sampleAccount(boolean admin) {
        Account account = new Account();
        account.setAccountID(admin ? 1 : 2);
        account.setFullName(admin ? "Quản trị viên Demo" : "Khách hàng Demo");
        account.setEmail(admin ? "admin@sportzone.demo" : "user@sportzone.demo");
        account.setPhone("0901234567");
        account.setStatus("Active");
        account.setRoleName(admin ? "Admin" : "User");
        return account;
    }

    private static List<Court> sampleCourts() {
        List<Court> courts = new ArrayList<Court>();
        for (int i = 1; i <= 10; i++) {
            boolean tennis = i <= 5;
            Court court = new Court();
            court.setCourtID(i);
            court.setCourtName((tennis ? "Tennis " : "Pickleball ") + ((i - 1) % 5 + 1));
            court.setTypeName(tennis ? "Tennis" : "Pickleball");
            court.setTypeSlug(tennis ? "tennis" : "pickleball");
            court.setLocation(tennis ? "Khu A" : "Khu B");
            court.setDescription("Sân thể thao chất lượng cao tại SportZone.");
            court.setPricePerHour(new BigDecimal(tennis ? "200000" : "150000"));
            court.setStatus("Available");
            court.setActive(true);
            courts.add(court);
        }
        return courts;
    }

    private static Court findCourt(List<Court> courts, String id, String courtId) {
        String value = id != null ? id : courtId;
        try {
            int index = Integer.parseInt(value) - 1;
            if (index >= 0 && index < courts.size()) return courts.get(index);
        } catch (NumberFormatException ignored) { }
        return courts.get(0);
    }

    private static LocalDate selectedDate(String value) {
        try {
            if (value != null) return LocalDate.parse(value);
        } catch (DateTimeParseException ignored) { }
        return LocalDate.now();
    }

    private static List<TimeSlot> sampleSlots(Court court, LocalDate date) {
        List<TimeSlot> slots = new ArrayList<TimeSlot>();
        for (int hour = 6; hour < 21; hour++) {
            TimeSlot slot = new TimeSlot(hour, court.getCourtId(), date, LocalTime.of(hour, 0),
                    LocalTime.of(hour + 1, 0), hour == 10 ? "Booked" : "Available", LocalDateTime.now());
            slots.add(slot);
        }
        return slots;
    }

    private static List<DateOption> dateOptions(LocalDate selected) {
        List<DateOption> options = new ArrayList<DateOption>();
        String[] days = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            options.add(new DateOption(date.toString(), days[date.getDayOfWeek().getValue() - 1],
                    date.format(DateTimeFormatter.ofPattern("dd/MM")), date.equals(selected)));
        }
        return options;
    }

    private static List<Service> sampleServices() {
        return Arrays.asList(new Service(1, "Thuê vợt", "", new BigDecimal("50000"), 60, true, LocalDateTime.now()),
                new Service(2, "Thuê bóng", "", new BigDecimal("30000"), 60, true, LocalDateTime.now()));
    }

    private static Booking sampleBooking() {
        Booking booking = new Booking();
        booking.setBookingID(1001);
        booking.setAccountID(2);
        booking.setCourtName("Tennis 1");
        booking.setAccountName("Khách hàng Demo");
        booking.setAccountRoleName("User");
        booking.setTypeName("Tennis");
        booking.setTypeSlug("tennis");
        booking.setLocation("Khu A");
        booking.setSlotDate(LocalDate.now().plusDays(1));
        booking.setStartTime(LocalTime.of(8, 0));
        booking.setEndTime(LocalTime.of(9, 0));
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("Confirmed");
        booking.setPaymentStatus("Paid");
        booking.setCourtPriceAtOrder(new BigDecimal("200000"));
        booking.setServiceTotalAtOrder(BigDecimal.ZERO);
        booking.setTotalAmount(new BigDecimal("200000"));
        return booking;
    }

    private static Invoice sampleInvoice() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceID(1);
        invoice.setBookingID(1001);
        invoice.setInvoiceCode("SZ-DEMO-1001");
        invoice.setAccountName("Khách hàng Demo");
        invoice.setCourtName("Tennis 1");
        invoice.setSlotDate(LocalDate.now().plusDays(1));
        invoice.setStartTime(LocalTime.of(8, 0));
        invoice.setEndTime(LocalTime.of(9, 0));
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setPaidAt(LocalDateTime.now());
        invoice.setCourtAmount(new BigDecimal("200000"));
        invoice.setFinalAmount(new BigDecimal("200000"));
        invoice.setPaymentStatus("Paid");
        invoice.setPaymentMethod("QR");
        return invoice;
    }
}
