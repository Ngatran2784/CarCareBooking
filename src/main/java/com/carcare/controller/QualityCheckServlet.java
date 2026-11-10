package com.carcare.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller: QualityCheckServlet
 * Kiểm định chất lượng nghiệm thu (Sprint 6)
 * Mapping: /quality-checks
 */
@WebServlet(name = "QualityCheckServlet", urlPatterns = {"/quality-checks"})
public class QualityCheckServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        // TODO: Triển khai xử lý nghiệp vụ chi tiết theo Sprint
        request.setAttribute("moduleName", "QualityCheck");
        request.setAttribute("action", action);

        request.getRequestDispatcher("/WEB-INF/views/job/quality-check.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO: Triển khai tiếp nhận POST form dữ liệu
        doGet(request, response);
    }
}
