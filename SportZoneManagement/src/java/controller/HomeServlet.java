package controller;

import dao.CourtDAO;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utils.CsrfUtil;
import utils.Views;

/**
 * Hien thi trang chu public voi so luong san active lay tu database.
 *
 * Request attributes:
 * - tennisCourtCount
 * - pickleballCourtCount
 *
 * Tag: U19
 */
public class HomeServlet extends HttpServlet {

    private final CourtDAO courtDAO;

    /**
     * Constructor Tomcat dung khi khoi tao servlet tu web.xml.
     */
    public HomeServlet() {
        this(new CourtDAO());
    }

    /**
     * Constructor dependency injection cho unit test.
     */
    HomeServlet(CourtDAO courtDAO) {
        if (courtDAO == null) {
            throw new IllegalArgumentException(
                    "CourtDAO must not be null"
            );
        }

        this.courtDAO = courtDAO;
    }

    /**
     * Xu ly noi dung dung chung cua GET va POST hien tai.
     *
     * Home khong thay doi state. doPost duoc giu lai de khong thay
     * doi behavior Phase 1 ngoai pham vi Phase 3.
     */
    protected void processRequest(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int tennisCourtCount;
        int pickleballCourtCount;

        /*
         * Lay ca hai count truoc khi bind request.
         * Neu mot query that bai, JSP khong nhan bo du lieu dang do
         * va khong thay bang count hard-code.
         */
        try {
            tennisCourtCount = courtDAO.countByType(
                    "tennis"
            );

            pickleballCourtCount = courtDAO.countByType(
                    "pickleball"
            );
        } catch (SQLException exception) {
            throw new ServletException(
                    "Unable to load home court counts",
                    exception
            );
        }

        /*
         * Layout va cac form public co the su dung token nay.
         * Giu nguyen behavior Phase 1.
         */
        CsrfUtil.ensureToken(
                request.getSession()
        );

        request.setAttribute(
                "tennisCourtCount",
                tennisCourtCount
        );

        request.setAttribute(
                "pickleballCourtCount",
                pickleballCourtCount
        );

        request.getRequestDispatcher(
                Views.HOME
        ).forward(request, response);
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Giu behavior hien tai cua Phase 1.
     *
     * Phase 3 khong them form POST cho home.
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        processRequest(request, response);
    }
}