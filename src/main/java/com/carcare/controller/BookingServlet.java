package com.carcare.controller;

import com.carcare.dal.CustomerDAO;
import com.carcare.dal.VehicleDAO;
import com.carcare.model.Customer;
import com.carcare.model.User;
import com.carcare.model.Vehicle;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller: BookingServlet
 * Quản lý Đặt lịch hẹn trực tuyến 
 * Mapping: /bookings
 */
@WebServlet(name = "BookingServlet", urlPatterns = {"/bookings"})
public class BookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            showVehicleSelection(request, response);
            return;
        }
        if (action == null) {
            action = "list";
        }

        // TODO: Triển khai xử lý nghiệp vụ chi tiết theo quy trình nghiệp vụ
        request.setAttribute("moduleName", "Booking");
        request.setAttribute("action", action);

        request.getRequestDispatcher("/WEB-INF/views/booking/list.jsp").forward(request, response);
    }

    private void showVehicleSelection(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = request.getSession(false) == null ? null
                : (User) request.getSession(false).getAttribute("currentUser");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        boolean frontDesk = user.hasRole("ADMIN") || user.hasRole("MANAGER") || user.hasRole("RECEPTIONIST");
        if (!frontDesk && !user.hasRole("CUSTOMER")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        VehicleDAO vehicles = new VehicleDAO();
        List<Vehicle> allowed;
        if (frontDesk) {
            allowed = vehicles.findAll();
        } else {
            Customer customer = new CustomerDAO().findByUserId(user.getUserId());
            allowed = customer == null ? List.of() : vehicles.findByCustomerId(customer.getCustomerId());
        }
        String selectedId = request.getParameter("vehicleId");
        Vehicle selected = null;
        if (selectedId != null && !selectedId.isBlank()) {
            for (Vehicle vehicle : allowed) {
                if (Integer.toString(vehicle.getVehicleId()).equals(selectedId)) {
                    selected = vehicle;
                    break;
                }
            }
            if (selected == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Xe không tồn tại hoặc bạn không có quyền chọn xe này.");
                return;
            }
        }
        request.setAttribute("vehicleList", allowed);
        request.setAttribute("selectedVehicle", selected);
        request.getRequestDispatcher("/WEB-INF/views/booking/form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO: Triển khai tiếp nhận POST form dữ liệu
        doGet(request, response);
    }
}
