package com.carcare.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller: CustomerServlet
 * Quản lý Hồ sơ khách hàng (Sprint 2)
 * Mapping: /customers
 */
@WebServlet(name = "CustomerServlet", urlPatterns = {"/customers"})
public class CustomerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        // TODO: Triển khai xử lý nghiệp vụ chi tiết theo Sprint
        request.setAttribute("moduleName", "Customer");
        request.setAttribute("action", action);

        request.getRequestDispatcher("/WEB-INF/views/customer/list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO: Triển khai tiếp nhận POST form dữ liệu
        doGet(request, response);
    }
}
