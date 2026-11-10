package com.carcare.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller: CheckInServlet
 * Tiếp nhận xe vào xưởng Check-in (Sprint 4/5)
 * Mapping: /checkin
 */
@WebServlet(name = "CheckInServlet", urlPatterns = {"/checkin"})
public class CheckInServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        // TODO: Triển khai xử lý nghiệp vụ chi tiết theo Sprint
        request.setAttribute("moduleName", "CheckIn");
        request.setAttribute("action", action);

        request.getRequestDispatcher("/WEB-INF/views/job/inspection.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO: Triển khai tiếp nhận POST form dữ liệu
        doGet(request, response);
    }
}
