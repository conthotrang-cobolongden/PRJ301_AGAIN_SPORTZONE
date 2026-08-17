package com.sportzone.controller;

import com.sportzone.dao.CourtDAO;
import com.sportzone.model.CourtType;
import com.sportzone.util.CsrfUtil;
import com.sportzone.util.Views;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * U7: danh sách sân, lọc theo loại (?type=all|tennis|pickleball).
 */
public class CourtsServlet extends HttpServlet {

    private final CourtDAO courtDAO = new CourtDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");
        if (type == null || type.trim().isEmpty()) {
            type = "all";
        }
        type = type.trim().toLowerCase();
        if (!"all".equals(type) && !"tennis".equals(type) && !"pickleball".equals(type)) {
            type = "all";
        }

        try {
            List<com.sportzone.model.Court> courtList = courtDAO.list(type);
            request.setAttribute("courtList", courtList);
            request.setAttribute("typeFilter", type);
            request.setAttribute("tennisCount", courtDAO.countByType(CourtType.TENNIS));
            request.setAttribute("pickleballCount", courtDAO.countByType(CourtType.PICKLEBALL));
        } catch (SQLException ex) {
            throw new ServletException("Lỗi tải danh sách sân: " + ex.getMessage(), ex);
        }

        CsrfUtil.ensureToken(request.getSession());
        request.getRequestDispatcher(Views.COURT_LIST).forward(request, response);
    }
}
