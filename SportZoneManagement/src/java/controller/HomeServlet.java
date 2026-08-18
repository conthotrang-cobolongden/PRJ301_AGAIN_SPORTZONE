package controller;

import utils.CsrfUtil;
import utils.Views;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Hien thi trang chu nen tang voi ton kho san co dinh. Tag: U19. */
public class HomeServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        CsrfUtil.ensureToken(request.getSession());
        request.setAttribute("tennisCourtCount", 5);
        request.setAttribute("pickleballCourtCount", 5);
        request.getRequestDispatcher(Views.HOME).forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
