package model;

/**
 * View model dai dien cho mot ngay trong day pill chon ngay
 * tren trang chi tiet san.
 *
 * Day khong phai entity database va khong anh xa toi mot bang SQL.
 * CourtDetailServlet tao danh sach DateOption tu ngay hien tai
 * den sau 6 ngay de court/detail.jsp hien thi.
 *
 * JSP contract:
 * - ${d.value}: gia tri ISO yyyy-MM-dd dung tren query string
 * - ${d.weekday}: nhan thu bang tieng Viet
 * - ${d.dayMonth}: ngay/thang hien thi theo dd/MM
 * - ${d.selected}: danh dau ngay dang duoc chon
 *
 * Tag: U9
 */
public final class DateOption {

    private final String value;
    private final String weekday;
    private final String dayMonth;
    private final boolean selected;

    /**
     * Tao mot lua chon ngay cho giao dien.
     *
     * @param value ngay dang ISO yyyy-MM-dd, vi du 2026-08-23
     * @param weekday nhan thu ngan gon, vi du CN hoac T2
     * @param dayMonth ngay/thang hien thi, vi du 23/08
     * @param selected true neu day la ngay dang duoc xem
     */
    public DateOption(
            String value,
            String weekday,
            String dayMonth,
            boolean selected
    ) {
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