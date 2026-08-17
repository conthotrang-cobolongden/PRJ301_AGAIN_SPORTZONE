package com.sportzone.controller;

import com.sportzone.util.HealthCheck;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HealthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean hashOk = HealthCheck.passwordHashMatches();
        String db = HealthCheck.pingDatabase();
        boolean dbOk = db.startsWith("OK");

        response.setContentType("text/plain; charset=UTF-8");
        response.setStatus(dbOk && hashOk ? HttpServletResponse.SC_OK : HttpServletResponse.SC_SERVICE_UNAVAILABLE);

        PrintWriter out = response.getWriter();
        out.println("SportZone health");
        out.println("SHA-256(Admin@123) = " + HealthCheck.passwordHash());
        out.println("Hash matches seed  = " + hashOk);
        out.println("Database           = " + db);
        out.flush();
    }
}
