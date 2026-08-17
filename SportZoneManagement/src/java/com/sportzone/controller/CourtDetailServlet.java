package com.sportzone.controller;

import com.sportzone.dao.CourtDAO;
import com.sportzone.dao.TimeSlotDAO;
import com.sportzone.model.Court;
import com.sportzone.model.DateOption;
import com.sportzone.model.TimeSlot;
import com.sportzone.util.CsrfUtil;
import com.sportzone.util.DateUtil;
import com.sportzone.util.Views;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * U8: chi tiết sân + U9: khung giờ theo ngày (?id=&date=yyyy-MM-dd).
 * Ngày xem bị kẹp trong [hôm nay, hôm nay + 6] — trùng phạm vi seed slot 7 ngày.
 */
public class CourtDetailServlet extends HttpServlet {

    private static final int MAX_DAYS_AHEAD = 6;

    private final CourtDAO courtDAO = new CourtDAO();
    private final TimeSlotDAO timeSlotDAO = new TimeSlotDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int courtId = parseInt(request.getParameter("id"), -1);
        if (courtId < 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Court court;
        try {
            court = courtDAO.findById(courtId);
        } catch (SQLException ex) {
            throw new ServletException("Lỗi tải chi tiết sân: " + ex.getMessage(), ex);
        }
        if (court == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Date today = DateUtil.today();
        Date maxDate = plusDays(today, MAX_DAYS_AHEAD);
        Date selectedDate = parseDate(request.getParameter("date"), today);
        if (selectedDate.before(today)) {
            selectedDate = today;
        } else if (selectedDate.after(maxDate)) {
            selectedDate = maxDate;
        }

        List<TimeSlot> slotList;
        try {
            slotList = timeSlotDAO.listByCourtAndDate(courtId, selectedDate);
        } catch (SQLException ex) {
            throw new ServletException("Lỗi tải khung giờ: " + ex.getMessage(), ex);
        }
        markPastSlots(slotList, selectedDate, today);

        request.setAttribute("court", court);
        request.setAttribute("selectedDate", DateUtil.format(selectedDate));
        request.setAttribute("selectedDateObj", selectedDate);
        request.setAttribute("minDate", DateUtil.format(today));
        request.setAttribute("maxDate", DateUtil.format(maxDate));
        request.setAttribute("dateOptions", buildDateOptions(today, MAX_DAYS_AHEAD, selectedDate));
        request.setAttribute("slotList", slotList);

        CsrfUtil.ensureToken(request.getSession());
        request.getRequestDispatcher(Views.COURT_DETAIL).forward(request, response);
    }

    /** U9: slot đã qua giờ trong ngày hiện tại → flag past để JSP thêm class is-past. */
    private void markPastSlots(List<TimeSlot> slots, Date selectedDate, Date today) {
        if (!selectedDate.equals(today)) {
            return;
        }
        long nowMillis = System.currentTimeMillis();
        for (TimeSlot slot : slots) {
            if (slot.getStartTime() != null && slot.getStartTime().getTime() <= nowMillis) {
                slot.setPast(true);
            }
        }
    }

    /** U9: dãy pill chọn ngày [hôm nay, hôm nay + daysAhead] cho JSP (demo kit C4). */
    private List<DateOption> buildDateOptions(Date today, int daysAhead, Date selectedDate) {
        SimpleDateFormat dayMonth = new SimpleDateFormat("dd/MM");
        List<DateOption> options = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        for (int i = 0; i <= daysAhead; i++) {
            Date current = new Date(calendar.getTimeInMillis());
            options.add(new DateOption(
                    DateUtil.format(current),
                    weekdayLabel(calendar.get(Calendar.DAY_OF_WEEK)),
                    dayMonth.format(current),
                    current.equals(selectedDate)));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return options;
    }

    private String weekdayLabel(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY: return "Chủ nhật";
            case Calendar.MONDAY: return "Thứ 2";
            case Calendar.TUESDAY: return "Thứ 3";
            case Calendar.WEDNESDAY: return "Thứ 4";
            case Calendar.THURSDAY: return "Thứ 5";
            case Calendar.FRIDAY: return "Thứ 6";
            default: return "Thứ 7";
        }
    }

    private Date parseDate(String raw, Date fallback) {
        if (raw == null || raw.trim().isEmpty()) {
            return fallback;
        }
        try {
            SimpleDateFormat parser = new SimpleDateFormat(DateUtil.DATE_PATTERN);
            parser.setLenient(false);
            return new Date(parser.parse(raw.trim()).getTime());
        } catch (ParseException ex) {
            return fallback;
        }
    }

    private Date plusDays(Date base, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(base);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return new Date(calendar.getTimeInMillis());
    }

    private int parseInt(String raw, int fallback) {
        if (raw == null || raw.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
