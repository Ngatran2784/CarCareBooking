package com.carcare.controller;

import com.carcare.dal.DBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller Trang chủ Dashboard (Sprint 1)
 * Mapping: /home
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra an toàn trạng thái kết nối cơ sở dữ liệu (không crash nếu chưa bật MySQL)
        boolean dbConnected = DBContext.isDatabaseConnected();
        request.setAttribute("dbConnected", dbConnected);
        request.setAttribute("appVersion", "1.0-SNAPSHOT");
        request.setAttribute("currentSprint", "Sprint 1 (Khởi tạo nền tảng & Kiến trúc MVC)");

        // Chuyển tiếp tới JSP hiển thị Dashboard
        request.getRequestDispatcher("/WEB-INF/views/dashboard/index.jsp").forward(request, response);
    }
}
