package controller;

import dao.CourtDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Court;
import utils.Views;

/**
 * Hien thi danh sach san public va filter theo Tennis/Pickleball.
 *
 * Route:
 * GET /courts
 *
 * Request:
 * - type=all
 * - type=tennis
 * - type=pickleball
 *
 * Request attributes:
 * - courtList
 * - typeFilter
 * - tennisCount
 * - pickleballCount
 *
 * Tag: U7
 */
public class CourtsServlet extends HttpServlet {

    private final CourtDAO courtDAO;

    /**
     * Constructor Tomcat su dung khi khoi tao servlet tu web.xml.
     */
    public CourtsServlet() {
        this(new CourtDAO());
    }

    /**
     * Constructor dependency injection dung cho test.
     *
     * Package-private de chi cac class trong package controller
     * va test cung package su dung.
     */
    CourtsServlet(CourtDAO courtDAO) {
        if (courtDAO == null) {
            throw new IllegalArgumentException(
                    "CourtDAO must not be null"
            );
        }

        this.courtDAO = courtDAO;
    }

    /**
     * GET /courts.
     *
     * Day la route public, khong yeu cau login va khong can CSRF
     * vi request chi doc du lieu.
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        String typeFilter = normalizeTypeFilter(
                request.getParameter("type")
        );

        /*
         * Doc het du lieu vao bien local truoc khi bind request.
         * Neu bat ky query nao that bai, JSP khong nhan bo du lieu
         * dang do va request duoc chuyen sang error handling.
         */
        List<Court> courtList;
        int tennisCount;
        int pickleballCount;

        try {
            courtList = courtDAO.findAll(typeFilter);

            tennisCount = courtDAO.countByType(
                    "tennis"
            );

            pickleballCount = courtDAO.countByType(
                    "pickleball"
            );
        } catch (SQLException exception) {
            throw new ServletException(
                    "Unable to load public court list",
                    exception
            );
        }

        request.setAttribute(
                "courtList",
                courtList
        );

        request.setAttribute(
                "typeFilter",
                typeFilter
        );

        request.setAttribute(
                "tennisCount",
                tennisCount
        );

        request.setAttribute(
                "pickleballCount",
                pickleballCount
        );

        request.getRequestDispatcher(
                Views.COURT_LIST
        ).forward(request, response);
    }

    /**
     * Whitelist filter truoc khi goi DAO.
     *
     * Gia tri hop le:
     * - tennis
     * - pickleball
     *
     * Moi gia tri khac, ke ca null/rong, deu tro thanh all.
     */
    private String normalizeTypeFilter(String rawType) {
        if (rawType == null) {
            return "all";
        }

        String normalized
                = rawType.trim().toLowerCase(Locale.ROOT);

        if ("tennis".equals(normalized)
                || "pickleball".equals(normalized)) {
            return normalized;
        }

        return "all";
    }
}