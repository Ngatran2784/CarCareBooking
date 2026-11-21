package com.carcare.controller;

import com.carcare.dal.CustomerDAO;
import com.carcare.dal.VehicleDAO;
import com.carcare.model.Customer;
import com.carcare.model.User;
import com.carcare.model.Vehicle;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Controller: VehicleServlet
 * Quản lý Phương tiện xe chăm sóc (US-03, S2-02, S2-07)
 * URL Mapping: /vehicles
 */
@WebServlet(name = "VehicleServlet", urlPatterns = {"/vehicles"})
public class VehicleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public static final List<String> VEHICLE_TYPES = Arrays.asList(
            "SEDAN", "SUV", "CUV", "MPV", "HATCHBACK", "PICKUP", "OTHER"
    );

    // Biển số xe Việt Nam: VD 30A-123.45, 51K-99999, 29B1-23456, 43A-1234
    private static final Pattern PLATE_PATTERN = Pattern.compile("^[0-9]{2}[A-Z0-9]{1,3}[- ]?[0-9]{3,5}([.][0-9]{2})?$");

    private VehicleDAO vehicleDAO;
    private CustomerDAO customerDAO;

    @Override
    public void init() throws ServletException {
        this.vehicleDAO = new VehicleDAO();
        this.customerDAO = new CustomerDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.isBlank()) {
            action = "list";
        }

        if ("list".equalsIgnoreCase(action)) {
            handleList(request, response, currentUser);
        } else if ("add".equalsIgnoreCase(action) || "form".equalsIgnoreCase(action)) {
            handleAddForm(request, response, currentUser);
        } else if ("edit".equalsIgnoreCase(action)) {
            handleEditForm(request, response, currentUser);
        } else if ("delete".equalsIgnoreCase(action)) {
            handleDelete(request, response, currentUser);
        } else {
            response.sendRedirect(request.getContextPath() + "/vehicles?action=list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String action = request.getParameter("action");
        if ("save".equalsIgnoreCase(action)) {
            handleSave(request, response, currentUser);
        } else if ("update".equalsIgnoreCase(action)) {
            handleUpdate(request, response, currentUser);
        } else if ("delete".equalsIgnoreCase(action)) {
            handleDelete(request, response, currentUser);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thao tác không được hỗ trợ.");
        }
    }

    // ==========================================
    // XỬ LÝ GET
    // ==========================================

    private void handleList(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        boolean isStaff = isStaffUser(currentUser);
        List<Vehicle> vehicles;

        if (!isStaff) {
            // Khách hàng: Chỉ xem xe của chính mình
            Customer customer = getOrCreateCustomer(currentUser);
            vehicles = vehicleDAO.findByCustomerId(customer.getCustomerId());
            request.setAttribute("isCustomerView", true);
            request.setAttribute("customer", customer);
            request.setAttribute("pageTitle", "Phương tiện của tôi - CarCareBooking");
        } else {
            // Nhân viên điều hành (ADMIN, MANAGER, RECEPTIONIST): Có thể lọc theo khách hàng hoặc từ khóa
            int filterCustomerId = parseInt(request.getParameter("customerId"), 0);
            String keyword = request.getParameter("keyword");

            if (filterCustomerId > 0) {
                vehicles = vehicleDAO.findByCustomerId(filterCustomerId);
                Customer c = customerDAO.findById(filterCustomerId);
                request.setAttribute("filterCustomer", c);
            } else if (keyword != null && !keyword.isBlank()) {
                vehicles = vehicleDAO.search(keyword.trim());
            } else {
                vehicles = vehicleDAO.findAll();
            }

            request.setAttribute("isCustomerView", false);
            request.setAttribute("keyword", keyword);
            request.setAttribute("filterCustomerId", filterCustomerId);
            request.setAttribute("pageTitle", "Quản lý xe khách hàng - CarCareBooking");
        }

        request.setAttribute("vehicleList", vehicles);
        request.getRequestDispatcher("/WEB-INF/views/vehicle/list.jsp").forward(request, response);
    }

    private void handleAddForm(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        boolean isStaff = isStaffUser(currentUser);
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleType("SEDAN");

        if (!isStaff) {
            Customer customer = getOrCreateCustomer(currentUser);
            vehicle.setCustomerId(customer.getCustomerId());
            request.setAttribute("isCustomerView", true);
            request.setAttribute("customer", customer);
            request.setAttribute("pageTitle", "Thêm xe mới - CarCareBooking");
        } else {
            int preselectCustomerId = parseInt(request.getParameter("customerId"), 0);
            if (preselectCustomerId > 0) {
                vehicle.setCustomerId(preselectCustomerId);
                Customer c = customerDAO.findById(preselectCustomerId);
                request.setAttribute("selectedCustomer", c);
            }
            List<Customer> customerList = customerDAO.findAll();
            request.setAttribute("customerList", customerList);
            request.setAttribute("isCustomerView", false);
            request.setAttribute("pageTitle", "Thêm phương tiện mới - CarCareBooking");
        }

        request.setAttribute("vehicle", vehicle);
        request.setAttribute("vehicleTypes", VEHICLE_TYPES);
        request.setAttribute("isNew", true);
        request.getRequestDispatcher("/WEB-INF/views/vehicle/form.jsp").forward(request, response);
    }

    private void handleEditForm(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        int vehicleId = parseInt(request.getParameter("id"), 0);
        Vehicle vehicle = vehicleDAO.findById(vehicleId);
        if (vehicle == null) {
            response.sendRedirect(request.getContextPath() + "/vehicles?action=list&error=" + urlEncode("Phương tiện không tồn tại."));
            return;
        }

        boolean isStaff = isStaffUser(currentUser);
        if (!isStaff) {
            Customer customer = getOrCreateCustomer(currentUser);
            // Kiểm tra quyền sở hữu phía Server
            if (!vehicleDAO.isOwner(vehicleId, customer.getCustomerId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền chỉnh sửa phương tiện của khách hàng khác.");
                return;
            }
            request.setAttribute("isCustomerView", true);
            request.setAttribute("customer", customer);
            request.setAttribute("pageTitle", "Chỉnh sửa xe: " + vehicle.getLicensePlate());
        } else {
            List<Customer> customerList = customerDAO.findAll();
            request.setAttribute("customerList", customerList);
            request.setAttribute("isCustomerView", false);
            request.setAttribute("pageTitle", "Chỉnh sửa phương tiện: " + vehicle.getLicensePlate());
        }

        request.setAttribute("vehicle", vehicle);
        request.setAttribute("vehicleTypes", VEHICLE_TYPES);
        request.setAttribute("isNew", false);
        request.getRequestDispatcher("/WEB-INF/views/vehicle/form.jsp").forward(request, response);
    }

    // ==========================================
    // XỬ LÝ POST
    // ==========================================

    private void handleSave(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        boolean isStaff = isStaffUser(currentUser);
        int targetCustomerId;

        if (!isStaff) {
            Customer customer = getOrCreateCustomer(currentUser);
            targetCustomerId = customer.getCustomerId();
        } else {
            targetCustomerId = parseInt(request.getParameter("customerId"), 0);
            if (targetCustomerId <= 0) {
                targetCustomerId = parseInt(request.getParameter("selectedCustomerId"), 0);
            }
        }

        String licensePlate = request.getParameter("licensePlate");
        String brand = request.getParameter("brand");
        String model = request.getParameter("model");
        String vehicleType = request.getParameter("vehicleType");
        String color = request.getParameter("color");
        int year = parseInt(request.getParameter("year"), 0);

        licensePlate = licensePlate != null ? licensePlate.trim().toUpperCase() : "";
        brand = brand != null ? brand.trim() : "";
        model = model != null ? model.trim() : "";
        vehicleType = vehicleType != null ? vehicleType.trim().toUpperCase() : "SEDAN";
        color = color != null ? color.trim() : "";

        Vehicle vehicle = new Vehicle();
        vehicle.setCustomerId(targetCustomerId);
        vehicle.setLicensePlate(licensePlate);
        vehicle.setBrand(brand);
        vehicle.setModel(model);
        vehicle.setVehicleType(vehicleType);
        vehicle.setColor(color);
        if (year > 0) vehicle.setYear(year);

        StringBuilder errors = validateVehicle(vehicle, null, targetCustomerId, isStaff);
        if (errors.length() > 0) {
            forwardWithError(request, response, currentUser, vehicle, true, errors.toString());
            return;
        }

        int genId = vehicleDAO.insert(vehicle);
        if (genId > 0) {
            response.sendRedirect(request.getContextPath() + "/vehicles?action=list&msg=" + urlEncode("Thêm phương tiện mới thành công!"));
        } else {
            forwardWithError(request, response, currentUser, vehicle, true, "Không thể lưu thông tin xe vào cơ sở dữ liệu.");
        }
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        int vehicleId = parseInt(request.getParameter("vehicleId"), 0);
        Vehicle existing = vehicleDAO.findById(vehicleId);
        if (existing == null) {
            response.sendRedirect(request.getContextPath() + "/vehicles?action=list&error=" + urlEncode("Phương tiện không tồn tại."));
            return;
        }

        boolean isStaff = isStaffUser(currentUser);
        int targetCustomerId = existing.getCustomerId();

        if (!isStaff) {
            Customer customer = getOrCreateCustomer(currentUser);
            // Kiểm tra bảo mật sở hữu xe
            if (!vehicleDAO.isOwner(vehicleId, customer.getCustomerId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền chỉnh sửa phương tiện này.");
                return;
            }
            targetCustomerId = customer.getCustomerId();
        } else {
            int formCustId = parseInt(request.getParameter("customerId"), 0);
            if (formCustId > 0) {
                targetCustomerId = formCustId;
            }
        }

        String licensePlate = request.getParameter("licensePlate");
        String brand = request.getParameter("brand");
        String model = request.getParameter("model");
        String vehicleType = request.getParameter("vehicleType");
        String color = request.getParameter("color");
        int year = parseInt(request.getParameter("year"), 0);

        licensePlate = licensePlate != null ? licensePlate.trim().toUpperCase() : "";
        brand = brand != null ? brand.trim() : "";
        model = model != null ? model.trim() : "";
        vehicleType = vehicleType != null ? vehicleType.trim().toUpperCase() : "SEDAN";
        color = color != null ? color.trim() : "";

        existing.setCustomerId(targetCustomerId);
        existing.setLicensePlate(licensePlate);
        existing.setBrand(brand);
        existing.setModel(model);
        existing.setVehicleType(vehicleType);
        existing.setColor(color);
        existing.setYear(year > 0 ? year : null);

        StringBuilder errors = validateVehicle(existing, vehicleId, targetCustomerId, isStaff);
        if (errors.length() > 0) {
            forwardWithError(request, response, currentUser, existing, false, errors.toString());
            return;
        }

        boolean ok = vehicleDAO.update(existing);
        if (ok) {
            response.sendRedirect(request.getContextPath() + "/vehicles?action=list&msg=" + urlEncode("Cập nhật thông tin xe thành công!"));
        } else {
            forwardWithError(request, response, currentUser, existing, false, "Không thể cập nhật thông tin xe.");
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        int vehicleId = parseInt(request.getParameter("id"), 0);
        if (vehicleId <= 0) {
            vehicleId = parseInt(request.getParameter("vehicleId"), 0);
        }

        Vehicle vehicle = vehicleDAO.findById(vehicleId);
        if (vehicle == null) {
            response.sendRedirect(request.getContextPath() + "/vehicles?action=list&error=" + urlEncode("Phương tiện không tồn tại."));
            return;
        }

        boolean isStaff = isStaffUser(currentUser);
        if (!isStaff) {
            Customer customer = getOrCreateCustomer(currentUser);
            if (!vehicleDAO.isOwner(vehicleId, customer.getCustomerId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền xóa phương tiện này.");
                return;
            }
        }

        boolean ok = vehicleDAO.delete(vehicleId);
        if (ok) {
            response.sendRedirect(request.getContextPath() + "/vehicles?action=list&msg=" + urlEncode("Đã xóa phương tiện thành công."));
        } else {
            response.sendRedirect(request.getContextPath() + "/vehicles?action=list&error=" + urlEncode("Không thể xóa phương tiện này do đã có lịch hẹn hoặc hồ sơ kỹ thuật liên quan."));
        }
    }

    // ==========================================
    // TIỆN ÍCH KIỂM TRA HỢP LỆ VÀ ĐIỀU HƯỚNG
    // ==========================================

    private StringBuilder validateVehicle(Vehicle vehicle, Integer currentVehicleId, int customerId, boolean isStaff) {
        StringBuilder errors = new StringBuilder();

        if (isStaff && customerId <= 0) {
            errors.append("Vui lòng chọn khách hàng sở hữu xe. ");
        }

        if (vehicle.getLicensePlate() == null || vehicle.getLicensePlate().isBlank()) {
            errors.append("Biển số xe bắt buộc phải nhập. ");
        } else if (!PLATE_PATTERN.matcher(vehicle.getLicensePlate()).matches()) {
            errors.append("Định dạng biển số xe không hợp lệ (Ví dụ: 30A-123.45 hoặc 51K-99999). ");
        } else if (vehicleDAO.existsByLicensePlate(vehicle.getLicensePlate(), currentVehicleId)) {
            errors.append("Biển số xe này đã tồn tại trên hệ thống. ");
        }

        if (vehicle.getBrand() == null || vehicle.getBrand().isBlank() || vehicle.getBrand().length() < 2) {
            errors.append("Hãng xe không được để trống (tối thiểu 2 ký tự). ");
        }

        if (vehicle.getModel() == null || vehicle.getModel().isBlank()) {
            errors.append("Dòng xe không được để trống. ");
        }

        if (vehicle.getVehicleType() == null || !VEHICLE_TYPES.contains(vehicle.getVehicleType().toUpperCase())) {
            errors.append("Phân loại xe không hợp lệ. ");
        }

        int maxYear = Year.now().getValue() + 1;
        if (vehicle.getYear() != null && vehicle.getYear() > 0) {
            if (vehicle.getYear() < 1980 || vehicle.getYear() > maxYear) {
                errors.append("Năm sản xuất phải từ 1980 đến ").append(maxYear).append(". ");
            }
        }

        return errors;
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, User currentUser,
                                  Vehicle vehicle, boolean isNew, String errorMessage)
            throws ServletException, IOException {
        boolean isStaff = isStaffUser(currentUser);
        request.setAttribute("vehicle", vehicle);
        request.setAttribute("vehicleTypes", VEHICLE_TYPES);
        request.setAttribute("isNew", isNew);
        request.setAttribute("isCustomerView", !isStaff);
        request.setAttribute("errorMessage", errorMessage);

        if (!isStaff) {
            Customer customer = getOrCreateCustomer(currentUser);
            request.setAttribute("customer", customer);
        } else {
            List<Customer> customerList = customerDAO.findAll();
            request.setAttribute("customerList", customerList);
        }

        request.setAttribute("pageTitle", isNew ? "Thêm xe mới - CarCareBooking" : "Chỉnh sửa xe - CarCareBooking");
        request.getRequestDispatcher("/WEB-INF/views/vehicle/form.jsp").forward(request, response);
    }

    private Customer getOrCreateCustomer(User user) {
        Customer c = customerDAO.findByUserId(user.getUserId());
        if (c == null) {
            c = new Customer();
            c.setUserId(user.getUserId());
            c.setFullName(user.getFullName());
            c.setPhone(user.getPhone());
            c.setEmail(user.getEmail());
            c.setMembershipLevel("STANDARD");
            int genId = customerDAO.insert(c);
            c = customerDAO.findById(genId);
        }
        return c;
    }

    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null ? (User) session.getAttribute("currentUser") : null;
    }

    private boolean isStaffUser(User user) {
        if (user == null) return false;
        return user.hasRole("ADMIN") || user.hasRole("MANAGER") || user.hasRole("RECEPTIONIST");
    }

    private int parseInt(String val, int defaultVal) {
        if (val == null || val.isBlank()) return defaultVal;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private String urlEncode(String text) {
        if (text == null) return "";
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }
}
