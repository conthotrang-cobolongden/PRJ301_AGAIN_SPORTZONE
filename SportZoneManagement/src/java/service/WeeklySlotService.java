package service;

import dao.TimeSlotDAO;
import java.sql.SQLException;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class WeeklySlotService {

    public static final String INVALID_WEEK = "INVALID_WEEK";
    private final TimeSlotDAO dao;
    private final Clock clock;

    public WeeklySlotService() { this(new TimeSlotDAO(), Clock.systemDefaultZone()); }
    public WeeklySlotService(TimeSlotDAO dao, Clock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public GenerateResult generate(LocalDate monday) throws SQLException {
        LocalDate today = LocalDate.now(clock);
        if (monday == null || monday.isBefore(today)
                || monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            return GenerateResult.failure(INVALID_WEEK,
                    "Ngày bắt đầu phải là thứ Hai và không thuộc quá khứ.");
        }
        TimeSlotDAO.GenerationResult result = dao.generateWeek(monday);
        return GenerateResult.success(result.getCreated(), result.getSkipped());
    }

    public static final class GenerateResult {
        private final boolean success;
        private final String errorCode;
        private final String message;
        private final int created;
        private final int skipped;

        private GenerateResult(boolean success, String errorCode,
                String message, int created, int skipped) {
            this.success = success;
            this.errorCode = errorCode;
            this.message = message;
            this.created = created;
            this.skipped = skipped;
        }
        public static GenerateResult success(int created, int skipped) {
            return new GenerateResult(true, null,
                    "Đã tạo " + created + " slot, bỏ qua " + skipped + ".",
                    created, skipped);
        }
        public static GenerateResult failure(String code, String message) {
            return new GenerateResult(false, code, message, 0, 0);
        }
        public boolean isSuccess() { return success; }
        public String getErrorCode() { return errorCode; }
        public String getMessage() { return message; }
        public int getCreated() { return created; }
        public int getSkipped() { return skipped; }
    }
}
