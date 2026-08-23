package dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import model.Court;

/**
 * Kiem tra CourtDAO tren SportZoneDB that.
 *
 * Contract Phase 3:
 * - Chi hien thi Courts.IsActive = 1.
 * - Ho tro filter tennis va pickleball.
 * - Van cho xem chi tiet san Maintenance/Unavailable neu IsActive = 1.
 * - JOIN CourtTypes de cung cap typeName va typeSlug.
 * - Sap xep danh sach on dinh theo CourtID.
 *
 * Tag: U7, U8, U19
 */
public final class CourtDAOContractTest {

    private CourtDAOContractTest() {
    }

    public static void main(String[] args) throws Exception {
        Class<?> daoType = loadCourtDAOType();
        Object courtDAO = daoType.getDeclaredConstructor().newInstance();

        int inactiveCourtID = 0;

        try {
            /*
             * Tao tam mot san IsActive = 0 de chung minh DAO khong
             * dua san da an ra giao dien public.
             */
            inactiveCourtID = insertInactiveCourt();

            List<Court> allCourts = invokeFindAll(
                    daoType,
                    courtDAO,
                    null
            );

            require(
                    allCourts.size() == 10,
                    "Public court list must contain exactly 10 active seed courts"
            );

            require(
                    !containsCourtID(allCourts, inactiveCourtID),
                    "Public court list must hide Courts.IsActive = 0"
            );

            require(
                    allCourts.get(0).getCourtID() == 1,
                    "Court list must be ordered by CourtID"
            );

            List<Court> tennisCourts = invokeFindAll(
                    daoType,
                    courtDAO,
                    "tennis"
            );

            require(
                    tennisCourts.size() == 5,
                    "Tennis filter must return exactly 5 active seed courts"
            );

            requireOnlyType(
                    tennisCourts,
                    "Tennis",
                    "tennis"
            );

            List<Court> pickleballCourts = invokeFindAll(
                    daoType,
                    courtDAO,
                    "pickleball"
            );

            require(
                    pickleballCourts.size() == 5,
                    "Pickleball filter must return exactly 5 active seed courts"
            );

            requireOnlyType(
                    pickleballCourts,
                    "Pickleball",
                    "pickleball"
            );

            /*
             * "all" phai co cung y nghia voi khong truyen filter.
             */
            List<Court> explicitAllCourts = invokeFindAll(
                    daoType,
                    courtDAO,
                    "all"
            );

            require(
                    explicitAllCourts.size() == allCourts.size(),
                    "Filter 'all' must return every active court"
            );

            Court maintenanceCourt = invokeFindById(
                    daoType,
                    courtDAO,
                    5
            );

            require(
                    maintenanceCourt != null,
                    "Active maintenance court must remain viewable"
            );

            require(
                    "Maintenance".equals(maintenanceCourt.getStatus()),
                    "Tennis Court 5 must preserve Maintenance status"
            );

            require(
                    "Tennis".equals(maintenanceCourt.getTypeName()),
                    "Court detail must include joined CourtTypes.TypeName"
            );

            require(
                    "tennis".equals(maintenanceCourt.getTypeSlug()),
                    "Court detail must expose lowercase typeSlug"
            );

            Court hiddenCourt = invokeFindById(
                    daoType,
                    courtDAO,
                    inactiveCourtID
            );

            require(
                    hiddenCourt == null,
                    "findById must hide Courts.IsActive = 0"
            );

            Court missingCourt = invokeFindById(
                    daoType,
                    courtDAO,
                    Integer.MAX_VALUE
            );

            require(
                    missingCourt == null,
                    "findById must return null for an unknown court ID"
            );

            int tennisCount = invokeCountByType(
                    daoType,
                    courtDAO,
                    "tennis"
            );

            int pickleballCount = invokeCountByType(
                    daoType,
                    courtDAO,
                    "pickleball"
            );

            require(
                    tennisCount == 5,
                    "Active Tennis court count must equal 5"
            );

            require(
                    pickleballCount == 5,
                    "Active Pickleball court count must equal 5"
            );
        } finally {
            if (inactiveCourtID > 0) {
                deleteContractCourt(inactiveCourtID);
            }
        }

        System.out.println("CourtDAOContractTest PASSED");
    }

    /**
     * Dung reflection de test van compile trong vong RED
     * khi CourtDAO chua ton tai.
     */
    private static Class<?> loadCourtDAOType() {
        try {
            return Class.forName("dao.CourtDAO");
        } catch (ClassNotFoundException exception) {
            throw new AssertionError(
                    "CourtDAO must exist for public court browsing",
                    exception
            );
        }
    }

    /**
     * Goi CourtDAO.findAll(String typeFilter).
     */
    @SuppressWarnings("unchecked")
    private static List<Court> invokeFindAll(
            Class<?> daoType,
            Object courtDAO,
            String typeFilter
    ) throws Exception {
        Method method = daoType.getMethod(
                "findAll",
                String.class
        );

        return (List<Court>) invoke(
                method,
                courtDAO,
                typeFilter
        );
    }

    /**
     * Goi CourtDAO.findById(int courtID).
     */
    private static Court invokeFindById(
            Class<?> daoType,
            Object courtDAO,
            int courtID
    ) throws Exception {
        Method method = daoType.getMethod(
                "findById",
                int.class
        );

        return (Court) invoke(
                method,
                courtDAO,
                courtID
        );
    }

    /**
     * Goi CourtDAO.countByType(String typeFilter).
     */
    private static int invokeCountByType(
            Class<?> daoType,
            Object courtDAO,
            String typeFilter
    ) throws Exception {
        Method method = daoType.getMethod(
                "countByType",
                String.class
        );

        return ((Integer) invoke(
                method,
                courtDAO,
                typeFilter
        )).intValue();
    }

    /**
     * Goi method reflection va nem lai nguyen nhan that neu DAO nem loi.
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
     * Chen mot san inactive tam thoi de test rule an san.
     */
    private static int insertInactiveCourt() throws Exception {
        String sql
                = "INSERT INTO Courts "
                + "(CourtTypeID, CourtName, Location, PricePerHour, "
                + "Description, ImageURL, Status, IsActive) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        String uniqueName
                = "Phase 3 Inactive Contract "
                + System.currentTimeMillis();

        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )) {
            statement.setInt(1, 1);
            statement.setString(2, uniqueName);
            statement.setString(3, "Contract Test");
            statement.setBigDecimal(
                    4,
                    new java.math.BigDecimal("100000")
            );
            statement.setString(
                    5,
                    "Temporary inactive court for contract testing"
            );
            statement.setString(6, null);
            statement.setString(7, "Available");
            statement.setBoolean(8, false);

            statement.executeUpdate();

            try (ResultSet generatedKeys
                    = statement.getGeneratedKeys()) {
                require(
                        generatedKeys.next(),
                        "Inactive contract court insert must return an ID"
                );

                return generatedKeys.getInt(1);
            }
        }
    }

    /**
     * Xoa du lieu tam ke ca khi assertion o giua test that bai.
     */
    private static void deleteContractCourt(int courtID)
            throws Exception {
        String sql
                = "DELETE FROM Courts "
                + "WHERE CourtID = ? "
                + "AND IsActive = 0";

        try (Connection connection = DBUtils.getConnection();
                PreparedStatement statement
                = connection.prepareStatement(sql)) {
            statement.setInt(1, courtID);
            statement.executeUpdate();
        }
    }

    private static boolean containsCourtID(
            List<Court> courts,
            int courtID
    ) {
        for (Court court : courts) {
            if (court.getCourtID() == courtID) {
                return true;
            }
        }

        return false;
    }

    /**
     * Kiem tra moi Court duoc map day du thong tin CourtTypes.
     */
    private static void requireOnlyType(
            List<Court> courts,
            String expectedTypeName,
            String expectedTypeSlug
    ) {
        for (Court court : courts) {
            require(
                    expectedTypeName.equals(court.getTypeName()),
                    "Filtered court must have typeName "
                    + expectedTypeName
            );

            require(
                    expectedTypeSlug.equals(court.getTypeSlug()),
                    "Filtered court must have typeSlug "
                    + expectedTypeSlug
            );
        }
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