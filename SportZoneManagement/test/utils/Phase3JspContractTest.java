package utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Kiem tra JSP contract cua Phase 3 ma khong can khoi dong Tomcat.
 *
 * Tag: U7, U8, U9
 */
public final class Phase3JspContractTest {

    private static final String DETAIL_JSP
            = "web/WEB-INF/views/court/detail.jsp";

    private Phase3JspContractTest() {
    }

    public static void main(String[] args) throws Exception {
        String detail = readUtf8(DETAIL_JSP);

        /*
         * TimeSlot.startTime la java.time.LocalTime.
         * fmt:formatDate khong ho tro java.time.LocalTime.
         */
        String incompatibleTimeFormatter
                = "<fmt:formatDate value=\"${s.startTime}\" "
                + "pattern=\"HH:mm\"/>";

        require(
                !detail.contains(incompatibleTimeFormatter),
                "court/detail.jsp must not pass LocalTime "
                + "to fmt:formatDate"
        );

        /*
         * SQL schema dung TIME(0), DAO map sang LocalTime.
         * LocalTime.toString() hien thi HH:mm khi seconds = 00.
         *
         * JSP co 4 nhanh hien slot time:
         * - Available
         * - Past
         * - Booked
         * - Closed
         */
        String safeTimeOutput
                = "<c:out value=\"${s.startTime}\"/>";

        require(
                countOccurrences(detail, safeTimeOutput) == 4,
                "court/detail.jsp must safely output startTime "
                + "in all four slot branches"
        );

        /*
         * selectedDateObj la java.sql.Date do servlet bind,
         * nen fmt:formatDate o day van hop le va phai duoc giu.
         */
        require(
                detail.contains(
                        "<fmt:formatDate "
                        + "value=\"${selectedDateObj}\" "
                        + "pattern=\"MM/yyyy\"/>"
                ),
                "selectedDateObj must retain MM/yyyy formatting"
        );

        /*
         * JSP phai tiep tuc dung cac bean properties camel-case
         * da duoc khoa trong ModelContractTest.
         */
        require(
                detail.contains("${court.courtId}"),
                "Court detail links must use court.courtId"
        );

        require(
                detail.contains("${s.timeSlotId}"),
                "Booking links must use s.timeSlotId"
        );

        require(
                detail.contains("${s.past}"),
                "Slot rendering must use the servlet-computed past flag"
        );

        System.out.println(
                "Phase3JspContractTest PASSED"
        );
    }

    private static String readUtf8(String relativePath)
            throws Exception {
        Path path = Paths.get(relativePath);

        return new String(
                Files.readAllBytes(path),
                StandardCharsets.UTF_8
        );
    }

    private static int countOccurrences(
            String source,
            String target
    ) {
        int count = 0;
        int startIndex = 0;

        while (true) {
            int foundIndex = source.indexOf(
                    target,
                    startIndex
            );

            if (foundIndex < 0) {
                return count;
            }

            count++;
            startIndex = foundIndex + target.length();
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