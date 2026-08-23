package utils;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import model.Booking;
import model.BookingService;
import model.Invoice;
import model.Service;

public final class Phase5BookingWebContractTest {

    private Phase5BookingWebContractTest() {
    }

    public static void main(String[] args)
            throws Exception {
        shouldExposeJspBeanProperties();
        shouldConfigureBookingRoutes();
        shouldProtectCreateBooking();
        shouldBindOwnedBookingLists();
        shouldEnforceBookingDetailOwnership();
        shouldKeepBookingJspScope();
        shouldConfigurePhase6SimulatedPayment();

        System.out.println(
                "Phase5BookingWebContractTest PASSED"
        );
    }

    private static void shouldExposeJspBeanProperties()
            throws Exception {
        requireReadableProperty(
                Booking.class,
                "bookingId"
        );
        requireReadableProperty(
                Booking.class,
                "slotDateValue"
        );
        requireReadableProperty(
                Booking.class,
                "startTimeValue"
        );
        requireReadableProperty(
                Booking.class,
                "endTimeValue"
        );
        requireReadableProperty(
                Booking.class,
                "bookingDateValue"
        );
        requireReadableProperty(
                Service.class,
                "serviceId"
        );
        requireReadableProperty(
                BookingService.class,
                "serviceId"
        );
    }

    private static void shouldConfigureBookingRoutes()
            throws Exception {
        String webXml = readProjectFile(
                "web/WEB-INF/web.xml"
        );
        String mainController = readProjectFile(
                "src/java/controller/MainController.java"
        );

        require(
                webXml.contains(
                        "controller.BookingCreateServlet"
                ),
                "web.xml phai khai bao BookingCreateServlet"
        );

        require(
                webXml.contains(
                        "controller.BookingsServlet"
                ),
                "web.xml phai khai bao BookingsServlet"
        );

        require(
                webXml.contains(
                        "controller.BookingDetailServlet"
                ),
                "web.xml phai khai bao BookingDetailServlet"
        );

        require(
                !webXml.contains("<url-pattern>/booking/create</url-pattern>")
                && !webXml.contains("<url-pattern>/bookings</url-pattern>")
                && !webXml.contains("<url-pattern>/booking</url-pattern>"),
                "Booking Servlet khong duoc co direct public mapping"
        );

        require(
                mainController.contains("case \"bookingCreate\":")
                && mainController.contains("case \"bookings\":")
                && mainController.contains("case \"bookingDetail\":"),
                "MainController phai dieu huong du ba booking action"
        );
    }

    private static void shouldProtectCreateBooking()
            throws Exception {
        String source = readProjectFile(
                "src/java/controller/BookingCreateServlet.java"
        );

        require(
                source.contains("CsrfUtil.isValid(request)"),
                "POST create booking phai kiem tra CSRF"
        );

        require(
                source.contains("SessionUtil.isFrozen("),
                "Frozen account phai bi chan"
        );

        require(
                source.contains("ROLE_USER"),
                "Chi role User duoc dat san"
        );

        require(
                source.contains("bookingService.create("),
                "Servlet phai delegate transaction cho BookingService"
        );

        require(
                !source.contains(
                        "bookingService.create(\n"
                        + "                    account.getAccountID(),\n"
                        + "                    requestedCourt"
                ),
                "Khong duoc truyen CourtID vao persistence"
        );

        require(
                source.contains(
                        "\"/MainController?action=bookingDetail&id=\""
                )
                && source.contains(
                        "\"&created=1\""
                ),
                "Create success phai PRG sang detail"
        );
    }

    private static void shouldBindOwnedBookingLists()
            throws Exception {
        String source = readProjectFile(
                "src/java/controller/BookingsServlet.java"
        );

        require(
                source.contains("bookingService.listMine("),
                "U11 phai lay booking theo owner"
        );

        require(
                source.contains("\"upcomingList\""),
                "Servlet phai bind upcomingList"
        );

        require(
                source.contains("\"historyList\""),
                "Servlet phai bind historyList"
        );

        require(
                source.contains("\"activeTab\""),
                "Servlet phai bind activeTab"
        );
    }

    private static void shouldEnforceBookingDetailOwnership()
            throws Exception {
        String source = readProjectFile(
                "src/java/controller/BookingDetailServlet.java"
        );

        require(
                source.contains("bookingService.getMine("),
                "U12 phai dung owner-scoped service"
        );

        require(
                source.contains("account.getAccountID()"),
                "U12 phai truyen AccountID hien tai"
        );

        require(
                source.contains("SC_NOT_FOUND"),
                "Booking sai owner phai tra 404"
        );

        require(
                source.contains("\"serviceLines\""),
                "Detail phai bind service snapshot lines"
        );
    }

    private static void shouldKeepBookingJspScope()
            throws Exception {
        String createJsp = readProjectFile(
                "web/WEB-INF/views/booking/create.jsp"
        );
        String listJsp = readProjectFile(
                "web/WEB-INF/views/booking/list.jsp"
        );
        String detailJsp = readProjectFile(
                "web/WEB-INF/views/booking/detail.jsp"
        );

        require(
                createJsp.contains(
                        "method=\"get\" action=\"${ctx}/MainController\""
                )
                && createJsp.contains(
                        "name=\"action\" value=\"bookingCreate\""
                )
                && createJsp.contains(
                        "method=\"post\" action=\"${ctx}/MainController?action=bookingCreate\""
                ),
                "Create JSP phai GET/POST qua MainController"
        );

        require(
                createJsp.contains(
                        "name=\"csrfToken\""
                ),
                "Create JSP phai gui CSRF"
        );

        require(
                createJsp.contains(
                        "name=\"slotId\""
                ),
                "Create JSP phai gui TimeSlotID"
        );

        require(
                createJsp.contains(
                        "name=\"serviceId\""
                ),
                "Create JSP phai gui selected services"
        );

        require(
                listJsp.contains(
                        "${ctx}/MainController?action=bookingDetail&amp;id=${b.bookingId}"
                ),
                "Booking list phai link sang owned detail"
        );

        require(
                detailJsp.contains(
                        "${booking.paymentStatus"
                ),
                "Payment display phai lay tu booking JOIN invoice"
        );

        require(
                !detailJsp.contains("/booking/cancel")
                && !detailJsp.contains("canCancel"),
                "Phase 5 khong co user cancel"
        );

        require(
                !detailJsp.contains("/invoice/notice"),
                "Payment notice chua nam trong Phase 5"
        );

        require(
                !detailJsp.contains("name=\"csrfToken\""),
                "Detail read-only khong can POST form"
        );
    }

    private static void shouldConfigurePhase6SimulatedPayment()
            throws Exception {
        String webXml = readProjectFile("web/WEB-INF/web.xml");
        String mainController = readProjectFile(
                "src/java/controller/MainController.java");
        String authFilter = readProjectFile(
                "src/java/filter/AuthFilter.java");
        String invoiceDAO = readProjectFile(
                "src/java/dao/InvoiceDAO.java");
        String paymentServlet = readProjectFile(
                "src/java/controller/InvoicePaymentServlet.java");
        String paymentJsp = readProjectFile(
                "web/WEB-INF/views/invoice/payment.jsp");
        String invoiceList = readProjectFile(
                "web/WEB-INF/views/invoice/list.jsp");

        requireReadableProperty(Invoice.class, "expiresAtEpochMilli");
        requireReadableProperty(Invoice.class, "paidAtValue");

        require(webXml.contains("controller.InvoicePaymentServlet")
                && webXml.contains("controller.InvoicesServlet")
                && webXml.contains("controller.InvoiceDetailServlet"),
                "web.xml phai khai bao named servlet Phase 6");
        require(!webXml.contains("<url-pattern>/invoice</url-pattern>")
                && !webXml.contains("<url-pattern>/invoices</url-pattern>")
                && !webXml.contains("<url-pattern>/invoice/*</url-pattern>"),
                "Phase 6 khong duoc co direct public mapping");
        require(mainController.contains("case \"invoicePayment\":")
                && mainController.contains("case \"invoices\":")
                && mainController.contains("case \"invoiceDetail\":"),
                "MainController phai dieu huong du ba action Phase 6");
        require(authFilter.contains("!\"invoicePayment\".equals(action)")
                && authFilter.contains("!\"invoices\".equals(action)")
                && authFilter.contains("!\"invoiceDetail\".equals(action)"),
                "AuthFilter phai bao ve action Phase 6");
        require(invoiceDAO.contains("DATEADD(MINUTE, 15, i.InvoiceDate)")
                && invoiceDAO.contains("PaymentStatus = N'Paid'")
                && invoiceDAO.contains("PaymentStatus = N'Cancelled'")
                && invoiceDAO.contains("Status = N'Cancelled'")
                && invoiceDAO.contains("Status = N'Available'")
                && invoiceDAO.contains("confirmPaidBooking(connection, bookingID, accountID)")
                && invoiceDAO.contains("Status = N'Confirmed'")
                && invoiceDAO.contains("b.Status = N'Pending'")
                && invoiceDAO.contains("connection.setAutoCommit(false)")
                && invoiceDAO.contains("b.AccountID = ?"),
                "InvoiceDAO phai atomically confirm Paid booking, cancel unpaid order, release slot va enforce ownership");
        require(paymentServlet.contains("CsrfUtil.isValid(request)")
                && paymentServlet.contains("invoiceService.pay(")
                && paymentServlet.contains("invoiceService.cancel("),
                "Payment servlet phai co CSRF va hai thao tac gia lap");
        require(paymentJsp.contains("payment-qr.svg")
                && paymentJsp.contains("paymentCountdown")
                && paymentJsp.contains("value=\"pay\"")
                && paymentJsp.contains("value=\"cancel\""),
                "Payment JSP phai co QR, countdown, pay va cancel");
        require(invoiceList.contains("Chỉ hiển thị hóa đơn có trạng thái Paid")
                && invoiceDAO.contains("i.PaymentStatus = N'Paid'"),
                "Danh sach hoa don chi duoc doc Paid");
    }

    private static void requireReadableProperty(
            Class<?> type,
            String propertyName
    ) throws Exception {
        for (PropertyDescriptor property
                : Introspector.getBeanInfo(type)
                        .getPropertyDescriptors()) {

            if (propertyName.equals(
                    property.getName()
            )
                    && property.getReadMethod() != null) {
                return;
            }
        }

        throw new AssertionError(
                type.getSimpleName()
                + " must expose JSP property "
                + propertyName
        );
    }

    private static String readProjectFile(
            String relativePath
    ) throws Exception {
        Path current = Paths.get("")
                .toAbsolutePath();

        while (current != null) {
            Path direct = current.resolve(
                    relativePath
            );

            if (Files.exists(direct)) {
                return new String(
                        Files.readAllBytes(direct),
                        StandardCharsets.UTF_8
                );
            }

            Path nested = current
                    .resolve("SportZoneManagement")
                    .resolve(relativePath);

            if (Files.exists(nested)) {
                return new String(
                        Files.readAllBytes(nested),
                        StandardCharsets.UTF_8
                );
            }

            current = current.getParent();
        }

        throw new IllegalStateException(
                "Khong tim thay: " + relativePath
        );
    }

    private static void require(
            boolean condition,
            String message
    ) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
