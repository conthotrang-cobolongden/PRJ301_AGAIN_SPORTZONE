package com.sportzone.util;

import java.sql.Date;
import java.text.SimpleDateFormat;

public final class DateUtil {

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    private DateUtil() {
    }

    public static Date today() {
        return new Date(System.currentTimeMillis());
    }

    public static String format(java.util.Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(DATE_PATTERN).format(date);
    }
}
