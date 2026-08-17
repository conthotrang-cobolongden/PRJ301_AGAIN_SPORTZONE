package com.sportzone.controller;

import com.sportzone.dao.CourtDAO;
import com.sportzone.model.CourtType;
import com.sportzone.util.CsrfUtil;
import com.sportzone.util.Views;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * U19: trang chủ — số sân lấy thật từ DB (P3), không hardcode.
 */
public class HomeServlet extends HttpServlet {

    private final CourtDAO courtDAO = new CourtDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CsrfUtil.ensureToken(request.getSession());
        try {
            request.setAttribute("tennisCourtCount", courtDAO.countByType(CourtType.TENNIS));
            request.setAttribute("pickleballCourtCount", courtDAO.countByType(CourtType.PICKLEBALL));
        } catch (SQLException ex) {
            throw new ServletException("Lỗi tải số liệu sân: " + ex.getMessage(), ex);
        }
        request.getRequestDispatcher(Views.HOME).forward(request, response);
    }
}
