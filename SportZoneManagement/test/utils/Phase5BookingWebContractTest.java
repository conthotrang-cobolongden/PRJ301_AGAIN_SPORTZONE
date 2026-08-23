package utils;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import model.Booking;
import model.BookingService;
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
                webXml.contains(
                        "<url-pattern>/booking/create</url-pattern>"
                ),
                "Phai map /booking/create"
        );

        require(
                webXml.contains(
                        "<url-pattern>/bookings</url-pattern>"
                ),
                "Phai map /bookings"
        );

        require(
                webXml.contains(
                        "<url-pattern>/booking</url-pattern>"
                ),
                "Phai map /booking"
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
                        "\"/booking?id=\""
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
                        "action=\"${ctx}/booking/create\""
                ),
                "Create JSP phai POST dung route"
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
                        "${ctx}/booking?id=${b.bookingId}"
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