package dao;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import model.Booking;
import model.Service;

/**
 * Batch test cho persistence Phase 5.
 *
 * Chi doc database, khong tao booking test.
 */
public final class Phase5BookingPersistenceTest {

    private Phase5BookingPersistenceTest() {
    }

    public static void main(String[] args) throws Exception {
        shouldLoadActiveServices();
        shouldReadOnlyOwnedBookings();
        shouldReadOwnedBookingDetail();
        shouldKeepCreateBookingContract();

        System.out.println(
                "Phase5BookingPersistenceTest PASSED"
        );
    }

    private static void shouldLoadActiveServices()
            throws Exception {
        List<Service> services
                = new ServiceDAO().findAllActive();

        require(
                !services.isEmpty(),
                "Seed database phai co active services"
        );

        for (Service service : services) {
            require(
                    service.isActive(),
                    "ServiceDAO chi duoc tra ve active service"
            );

            require(
                    service.getPrice() != null
                    && service.getPrice().signum() >= 0,
                    "Service price phai duoc map bang BigDecimal"
            );
        }
    }

    private static void shouldReadOnlyOwnedBookings()
            throws Exception {
        BookingDAO bookingDAO = new BookingDAO();

        List<Booking> userOneBookings
                = bookingDAO.findByAccountID(2);

        require(
                !userOneBookings.isEmpty(),
                "Seed user AccountID=2 phai co booking"
        );

        for (Booking booking : userOneBookings) {
            require(
                    booking.getAccountID() == 2,
                    "U11 chi duoc tra booking cua dung account"
            );

            require(
                    booking.getCourtName() != null,
                    "Booking list phai map thong tin court"
            );

            require(
                    booking.getSlotDate() != null,
                    "Booking list phai map slot date"
            );

            require(
                    booking.getTotalAmount() != null,
                    "Booking list phai map snapshot total"
            );
        }

        require(
                bookingDAO.findByAccountID(
                        Integer.MAX_VALUE
                ).isEmpty(),
                "Account khong ton tai khong duoc thay booking"
        );
    }

    private static void shouldReadOwnedBookingDetail()
            throws Exception {
        BookingDAO bookingDAO = new BookingDAO();

        Booking summary = bookingDAO
                .findByAccountID(2)
                .get(0);

        Booking owned = bookingDAO
                .findDetailByIDAndAccountID(
                        summary.getBookingID(),
                        2
                );

        require(
                owned != null,
                "Owner phai xem duoc booking detail"
        );

        require(
                owned.getBookingID()
                == summary.getBookingID(),
                "Booking detail phai dung BookingID"
        );

        Booking foreignAccess = bookingDAO
                .findDetailByIDAndAccountID(
                        summary.getBookingID(),
                        Integer.MAX_VALUE
                );

        require(
                foreignAccess == null,
                "User khac khong duoc xem booking detail"
        );

        require(
                bookingDAO
                        .findServicesByBookingIDAndAccountID(
                                summary.getBookingID(),
                                Integer.MAX_VALUE
                        )
                        .isEmpty(),
                "User khac khong duoc xem booking services"
        );
    }

    private static void shouldKeepCreateBookingContract()
            throws Exception {
        String source = readProjectFile(
                "src/java/dao/BookingDAO.java"
        );

        String normalized = source
                .replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT);

        require(
                normalized.contains(
                        "WITH (UPDLOCK, HOLDLOCK)"
                ),
                "Tao booking phai lock slot/account"
        );

        require(
                normalized.contains(
                        "INSERT INTO BOOKINGS"
                ),
                "BookingDAO phai insert Bookings"
        );

        require(
                normalized.contains(
                        "INSERT INTO BOOKINGSERVICES"
                ),
                "BookingDAO phai insert BookingServices"
        );

        require(
                normalized.contains(
                        "SET STATUS = N'BOOKED'"
                ),
                "BookingDAO phai chuyen slot sang Booked"
        );

        require(
                normalized.contains(
                        "COURTPRICEATORDER"
                )
                && normalized.contains(
                        "PRICEATORDER"
                ),
                "Booking phai snapshot gia court va service"
        );

        require(
                !normalized.contains(
                        "INSERT INTO INVOICES"
                ),
                "U10 tuyet doi khong duoc tao Invoice"
        );

        require(
                source.contains("2601")
                && source.contains("2627")
                && source.contains(
                        "UQ_BOOKINGS_SLOT_ACTIVE"
                ),
                "BookingDAO phai nhan dien double-book conflict"
        );

        require(
                source.contains(
                        "b.AccountID = ?"
                ),
                "Booking detail phai kiem tra ownership"
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
                "Khong tim thay file: " + relativePath
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