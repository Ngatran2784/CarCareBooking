package com.carcare.controller;

import com.carcare.dal.DBContext;
import com.carcare.dal.ReportDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Controller: HomeServlet
 * Hiển thị Bảng điều khiển nghiệp vụ (Dashboard) cho hệ thống quản lý chăm sóc xe.
 * Mapping: /home
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ReportDAO reportDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.reportDAO = new ReportDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra an toàn trạng thái kết nối cơ sở dữ liệu
        boolean dbConnected = DBContext.isDatabaseConnected();
        request.setAttribute("dbConnected", dbConnected);

        // Truy vấn số liệu thống kê vận hành thực tế
        Map<String, Object> stats = reportDAO.getDashboardStatistics();
        request.setAttribute("stats", stats);

        // Truy vấn trạng thái trực quan các khoang dịch vụ
        List<Map<String, String>> bays = reportDAO.getServiceBaysOverview();
        request.setAttribute("bays", bays);

        // Chuyển tiếp tới giao diện Dashboard
        request.getRequestDispatcher("/WEB-INF/views/dashboard/index.jsp").forward(request, response);
    }
}
