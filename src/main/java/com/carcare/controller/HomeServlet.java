package com.carcare.controller;

import java.io.IOException;

import com.carcare.dal.DBContext;
import com.carcare.dal.ReportDAO;
import com.carcare.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

        // Xác định loại Dashboard theo vai trò (ưu tiên vai trò quyền cao nhất)
        User user = (User) request.getSession().getAttribute("currentUser");
        String dashboardRole = resolveDashboardRole(user);
        request.setAttribute("dashboardRole", dashboardRole);

        // Chỉ truy vấn số liệu phù hợp với vai trò, khách hàng không được xem số liệu toàn xưởng
        switch (dashboardRole) {
            case "ADMIN":
            case "MANAGER":
            case "RECEPTIONIST":
                request.setAttribute("stats", reportDAO.getDashboardStatistics());
                request.setAttribute("bays", reportDAO.getServiceBaysOverview());
                break;
            case "TECHNICIAN":
                request.setAttribute("stats", reportDAO.getTechnicianStatistics(user.getUserId()));
                break;
            default:
                request.setAttribute("stats", reportDAO.getCustomerStatistics(user.getUserId()));
                break;
        }

        // Chuyển tiếp tới giao diện Dashboard
        request.getRequestDispatcher("/WEB-INF/views/dashboard/index.jsp").forward(request, response);
    }

    /** Chọn Dashboard hiển thị theo vai trò. */
    private String resolveDashboardRole(User user) {
        if (user == null) return "CUSTOMER";
        if (user.hasRole("ADMIN")) return "ADMIN";
        if (user.hasRole("MANAGER")) return "MANAGER";
        if (user.hasRole("RECEPTIONIST")) return "RECEPTIONIST";
        if (user.hasRole("TECHNICIAN")) return "TECHNICIAN";
        return "CUSTOMER";
    }
}
