package com.sportzone.model;

/**
 * U9: một lựa chọn trong dãy pill chọn ngày ở trang chi tiết sân.
 * Chỉ là view model — servlet tự sinh từ [hôm nay, hôm nay + 6], không truy vấn DB.
 */
public class DateOption {

    private final String value;      // yyyy-MM-dd — dùng trong href
    private final String weekday;    // "Thứ 2".."Chủ nhật"
    private final String dayMonth;   // "17/08"
    private final boolean selected;

    public DateOption(String value, String weekday, String dayMonth, boolean selected) {
        this.value = value;
        this.weekday = weekday;
        this.dayMonth = dayMonth;
        this.selected = selected;
    }

    public String getValue() {
        return value;
    }

    public String getWeekday() {
        return weekday;
    }

    public String getDayMonth() {
        return dayMonth;
    }

    public boolean isSelected() {
        return selected;
    }
}
