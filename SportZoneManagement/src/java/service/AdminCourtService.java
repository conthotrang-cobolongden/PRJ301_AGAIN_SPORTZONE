package service;

import dao.AdminCourtDAO;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.Court;

public class AdminCourtService {

    public static final String INVALID_INPUT = "INVALID_INPUT";
    public static final String INVALID_PRICE = "INVALID_PRICE";
    public static final String INVALID_STATUS = "INVALID_STATUS";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String COURT_HAS_FUTURE_BOOKINGS
            = "COURT_HAS_FUTURE_BOOKINGS";
    private static final Set<String> STATUSES = new HashSet<>(Arrays.asList(
            "Available", "Maintenance", "Unavailable"));
    private final AdminCourtDAO dao;

    public AdminCourtService() { this(new AdminCourtDAO()); }
    public AdminCourtService(AdminCourtDAO dao) { this.dao = dao; }

    public List<Court> list() throws SQLException { return dao.findAll(); }

    public UpdateResult update(int courtID, String rawPrice, String status,
            LocalDateTime now) throws SQLException {
        if (courtID <= 0 || now == null) {
            return UpdateResult.failure(INVALID_INPUT, 0,
                    "Dữ liệu sân không hợp lệ.");
        }
        BigDecimal price;
        try {
            price = new BigDecimal(rawPrice == null ? "" : rawPrice.trim());
        } catch (NumberFormatException exception) {
            return UpdateResult.failure(INVALID_PRICE, 0,
                    "Giá sân không hợp lệ.");
        }
        if (price.signum() < 0) {
            return UpdateResult.failure(INVALID_PRICE, 0,
                    "Giá sân phải lớn hơn hoặc bằng 0.");
        }
        if (!STATUSES.contains(status)) {
            return UpdateResult.failure(INVALID_STATUS, 0,
                    "Trạng thái sân không hợp lệ.");
        }
        AdminCourtDAO.CourtUpdateOutcome outcome
                = dao.updatePriceAndStatus(courtID, price, status, now);
        if (!outcome.isFound()) {
            return UpdateResult.failure(NOT_FOUND, 0, "Không tìm thấy sân.");
        }
        if (outcome.getBlockingCount() > 0) {
            return UpdateResult.failure(COURT_HAS_FUTURE_BOOKINGS,
                    outcome.getBlockingCount(),
                    "Không thể ngừng sân vì đang có "
                    + outcome.getBlockingCount() + " booking tương lai.");
        }
        return outcome.isUpdated()
                ? UpdateResult.success()
                : UpdateResult.failure(INVALID_INPUT, 0,
                        "Không thể cập nhật sân.");
    }

    public static final class UpdateResult {
        private final boolean success;
        private final String errorCode;
        private final int blockingCount;
        private final String message;

        private UpdateResult(boolean success, String errorCode,
                int blockingCount, String message) {
            this.success = success;
            this.errorCode = errorCode;
            this.blockingCount = blockingCount;
            this.message = message;
        }
        public static UpdateResult success() {
            return new UpdateResult(true, null, 0, "Cập nhật sân thành công.");
        }
        public static UpdateResult failure(String code, int count,
                String message) {
            return new UpdateResult(false, code, count, message);
        }
        public boolean isSuccess() { return success; }
        public String getErrorCode() { return errorCode; }
        public int getBlockingCount() { return blockingCount; }
        public String getMessage() { return message; }
    }
}
