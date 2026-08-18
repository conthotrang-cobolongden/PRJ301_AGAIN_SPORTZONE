package com.sportzone.controller;

import com.sportzone.util.HealthCheck;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Tra trang thai database, schema va password hash cua Phase 1. */
public class HealthServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            try {
                HealthCheck.Result result = HealthCheck.run();
                response.setStatus(result.isHealthy() ? HttpServletResponse.SC_OK
                        : HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                out.println(result.isHealthy() ? "OK" : "DEGRADED");
                out.println("database=" + (result.isDatabaseOk() ? "OK" : "FAIL"));
                out.println("tables=" + result.getTableCount());
                out.println("passwordHash=" + (result.isPasswordHashOk() ? "OK" : "FAIL"));
            } catch (Exception exception) {
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                out.println("FAIL");
                out.println("database=FAIL");
            }
        }
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
