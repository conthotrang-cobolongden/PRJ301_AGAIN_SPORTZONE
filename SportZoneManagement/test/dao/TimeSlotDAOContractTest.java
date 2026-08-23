package dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import model.TimeSlot;

/**
 * Kiem tra TimeSlotDAO tren SportZoneDB that.
 *
 * Contract Phase 3:
 * - Lay slot theo dung CourtID va SlotDate.
 * - Tra ve ca Available, Booked va Closed cho JSP hien thi.
 * - Sap xep tang dan theo StartTime.
 * - Map dung DATE, TIME va DATETIME sang java.time.
 * - DAO khong tu danh dau past; do la trach nhiem cua servlet.
 *
 * Tag: U9
 */
public final class TimeSlotDAOContractTest {

    private TimeSlotDAOContractTest() {
    }

    public static void main(String[] args) throws Exception {
        Class<?> daoType = loadTimeSlotDAOType();
        Object timeSlotDAO
                = daoType.getDeclaredConstructor().newInstance();

        /*
         * Khong phu thuoc LocalDate.now() cua may chay test.
         * Lay ngay seed nho nhat truc tiep tu database.
         */
        LocalDate seedDate = findFirstSeedDate(1);

        require(
                seedDate != null,
                "Court 1 must have seeded time slots"
        );

        List<TimeSlot> courtOneSlots = invokeList(
                daoType,
                timeSlotDAO,
                1,
                seedDate
        );

        require(
                courtOneSlots.size() == 8,
                "Court 1 must have exactly 8 seeded slots per day"
        );

        requireSlotsBelongTo(
                courtOneSlots,
                1,
                seedDate
        );

        requireAscendingStartTime(courtOneSlots);

        require(
                LocalTime.of(6, 0).equals(
                        courtOneSlots.get(0).getStartTime()
                ),
                "First seeded slot must start at 06:00"
        );

        require(
                LocalTime.of(7, 0).equals(
                        courtOneSlots.get(0).getEndTime()
                ),
                "First seeded slot must end at 07:00"
        );

        require(
                LocalTime.of(20, 0).equals(
                        courtOneSlots.get(
                                courtOneSlots.size() - 1
                        ).getStartTime()
                ),
                "Last seeded slot must start at 20:00"
        );

        require(
                containsStatus(courtOneSlots, "Available"),
                "Court 1 seed must expose Available slots"
        );

        require(
                containsStatus(courtOneSlots, "Booked"),
                "Court 1 seed must expose Booked slots"
        );

        /*
         * past la view-only flag. DAO chi map du lieu database;
         * CourtDetailServlet se tinh past theo thoi gian hien tai.
         */
        for (TimeSlot slot : courtOneSlots) {
            require(
                    !slot.isPast(),
                    "TimeSlotDAO must not mark past slots"
            );

            require(
                    slot.getCreatedAt() != null,
                    "TimeSlot CreatedAt must be mapped"
            );
        }

        List<TimeSlot> maintenanceSlots = invokeList(
                daoType,
                timeSlotDAO,
                5,
                seedDate
        );

        require(
                maintenanceSlots.size() == 8,
                "Maintenance court must retain its 8 seeded slots"
        );

        for (TimeSlot slot : maintenanceSlots) {
            require(
                    "Closed".equals(slot.getStatus()),
                    "Maintenance court slots must remain Closed"
            );
        }

        List<TimeSlot> unknownCourtSlots = invokeList(
                daoType,
                timeSlotDAO,
                Integer.MAX_VALUE,
                seedDate
        );

        require(
                unknownCourtSlots.isEmpty(),
                "Unknown court must return an empty slot list"
        );

        requireNullDateRejected(
                daoType,
                timeSlotDAO
        );

        System.out.println("TimeSlotDAOContractTest PASSED");
    }

    /**
     * Dung reflection de test van compile trong vong RED
     * khi TimeSlotDAO chua ton tai.
     */
    private static Class<?> loadTimeSlotDAOType() {
        try {
            return Class.forName("dao.TimeSlotDAO");
        } catch (ClassNotFoundException exception) {
            throw new AssertionError(
                    "TimeSlotDAO must exist for public schedule browsing",
                    exception
            );
        }
    }

    /**
     * Goi:
     * TimeSlotDAO.listByCourtAndDate(int courtID, LocalDate slotDate)
     */
    @SuppressWarnings("unchecked")
    private static List<TimeSlot> invokeList(
            Class<?> daoType,
            Object timeSlotDAO,
            int courtID,
            LocalDate slotDate
    ) throws Exception {
        Method method = daoType.getMethod(
                "listByCourtAndDate",
                int.class,
                LocalDate.class
        );

        return (List<TimeSlot>) invoke(
                method,
                timeSlotDAO,
                courtID,
                slotDate
        );
    }

    /**
     * Null date la loi lap trinh va phai bi chan truoc khi gui SQL.
     */
    private static void requireNullDateRejected(
            Class<?> daoType,
            Object timeSlotDAO
    ) throws Exception {
        boolean rejected = false;

        try {
            invokeList(
                    daoType,
                    timeSlotDAO,
                    1,
                    null
            );
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }

        require(
                rejected,
                "listByCourtAndDate must reject a null date"
        );
    }

    /**
     * Goi reflection va nem lai nguyen nhan that tu DAO.
     */
    private static Object invoke(
            Method method,
            Object target,
            Object... arguments
    ) throws Exception {
        try {
            return method.invoke(target, arguments);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();

            if (cause instanceof Exception) {
                throw (Exception) cause;
            }

            if (cause instanceof Error) {
                throw (Error) cause;
            }

            throw exception;
        }
    }

    /**
     * Lay ngay nho nhat co seed cho mot san.
     */
    private static LocalDate findFirstSeedDate(int courtID)
            throws Exception {
        String sql
                = "SELECT MIN(SlotDate) AS FirstSlotDate "
                + "FROM TimeSlots "
                + "WHERE CourtID = ?";

        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(sql)) {
            statement.setInt(1, courtID);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                java.sql.Date firstDate
                        = resultSet.getDate("FirstSlotDate");

                return firstDate == null
                        ? null
                        : firstDate.toLocalDate();
            }
        }
    }

    private static void requireSlotsBelongTo(
            List<TimeSlot> slots,
            int expectedCourtID,
            LocalDate expectedDate
    ) {
        for (TimeSlot slot : slots) {
            require(
                    slot.getCourtID() == expectedCourtID,
                    "Every slot must belong to requested CourtID"
            );

            require(
                    expectedDate.equals(slot.getSlotDate()),
                    "Every slot must belong to requested SlotDate"
            );
        }
    }

    private static void requireAscendingStartTime(
            List<TimeSlot> slots
    ) {
        for (int index = 1; index < slots.size(); index++) {
            LocalTime previous
                    = slots.get(index - 1).getStartTime();

            LocalTime current
                    = slots.get(index).getStartTime();

            require(
                    !current.isBefore(previous),
                    "Slots must be ordered by StartTime"
            );
        }
    }

    private static boolean containsStatus(
            List<TimeSlot> slots,
            String expectedStatus
    ) {
        for (TimeSlot slot : slots) {
            if (expectedStatus.equals(slot.getStatus())) {
                return true;
            }
        }

        return false;
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