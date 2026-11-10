package com.carcare.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller: ServiceBayServlet
 * Quản lý Khoang và Cầu dịch vụ 
 * Mapping: /bays
 */
@WebServlet(name = "ServiceBayServlet", urlPatterns = {"/bays"})
public class ServiceBayServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        // TODO: Triển khai xử lý nghiệp vụ chi tiết theo quy trình nghiệp vụ
        request.setAttribute("moduleName", "ServiceBay");
        request.setAttribute("action", action);

        request.getRequestDispatcher("/WEB-INF/views/bay/list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO: Triển khai tiếp nhận POST form dữ liệu
        doGet(request, response);
    }
}
