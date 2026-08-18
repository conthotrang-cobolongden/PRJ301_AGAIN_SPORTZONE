package utils;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Chuyen doi va so sanh ngay gio cua time slot.
 */
public final class DateUtil {

    private DateUtil() {
    }

    public static LocalDateTime combine(LocalDate date, LocalTime time) {
        if (date == null || time == null) {
            throw new IllegalArgumentException("Date and time must not be null");
        }
        return LocalDateTime.of(date, time);
    }

    public static LocalDateTime combine(Date date, Time time) {
        return combine(date.toLocalDate(), time.toLocalTime());
    }

    public static boolean isWithinInclusive(LocalDate value, LocalDate start, LocalDate end) {
        return value != null && start != null && end != null
                && !value.isBefore(start) && !value.isAfter(end);
    }

    public static boolean hasStarted(LocalDate date, LocalTime startTime, LocalDateTime now) {
        return !combine(date, startTime).isAfter(now);
    }

    public static Date toSqlDate(LocalDate value) {
        return value == null ? null : Date.valueOf(value);
    }

    public static Time toSqlTime(LocalTime value) {
        return value == null ? null : Time.valueOf(value);
    }

    public static Timestamp toSqlTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }
}
