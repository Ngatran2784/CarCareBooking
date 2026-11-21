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
import java.util.List;
import java.util.regex.Pattern;

/**
 * Controller: CustomerServlet
 * Quản lý Hồ sơ khách hàng (US-03)
 * URL Mapping: /customers
 */
@WebServlet(name = "CustomerServlet", urlPatterns = {"/customers"})
public class CustomerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    private CustomerDAO customerDAO;
    private VehicleDAO vehicleDAO;

    @Override
    public void init() throws ServletException {
        this.customerDAO = new CustomerDAO();
        this.vehicleDAO = new VehicleDAO();
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
        boolean isStaff = isStaffUser(currentUser);

        // Khách hàng mặc định vào trang hồ sơ cá nhân
        if (!isStaff) {
            if (action == null || action.isBlank() || "profile".equalsIgnoreCase(action) || "detail".equalsIgnoreCase(action)) {
                handleCustomerProfile(request, response, currentUser);
                return;
            }
            // Khách hàng không được phép truy cập action list hoặc của staff
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập chức năng này.");
            return;
        }

        // Dành cho nhân viên (ADMIN, MANAGER, RECEPTIONIST)
        if (action == null || action.isBlank() || "list".equalsIgnoreCase(action)) {
            handleStaffList(request, response);
        } else if ("detail".equalsIgnoreCase(action)) {
            handleStaffDetail(request, response);
        } else if ("add".equalsIgnoreCase(action) || "form".equalsIgnoreCase(action)) {
            handleStaffAddForm(request, response);
        } else if ("edit".equalsIgnoreCase(action)) {
            handleStaffEditForm(request, response);
        } else if ("delete".equalsIgnoreCase(action)) {
            handleStaffDelete(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/customers?action=list");
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
        boolean isStaff = isStaffUser(currentUser);

        if ("updateProfile".equalsIgnoreCase(action)) {
            handleUpdateProfile(request, response, currentUser);
        } else if (isStaff && "save".equalsIgnoreCase(action)) {
            handleStaffSave(request, response);
        } else if (isStaff && "update".equalsIgnoreCase(action)) {
            handleStaffUpdate(request, response);
        } else if (isStaff && "delete".equalsIgnoreCase(action)) {
            handleStaffDelete(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Hành động không hợp lệ.");
        }
    }

    // ==========================================
    // CÁC HÀM XỬ LÝ KHÁCH HÀNG (CUSTOMER)
    // ==========================================

    private void handleCustomerProfile(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        Customer customer = customerDAO.findByUserId(currentUser.getUserId());
        if (customer == null) {
            // Tự động khởi tạo hồ sơ nếu người dùng chưa có bản ghi customers
            customer = new Customer();
            customer.setUserId(currentUser.getUserId());
            customer.setFullName(currentUser.getFullName());
            customer.setPhone(currentUser.getPhone());
            customer.setEmail(currentUser.getEmail());
            customer.setMembershipLevel("STANDARD");
            int genId = customerDAO.insert(customer);
            customer = customerDAO.findById(genId);
        }

        List<Vehicle> vehicles = vehicleDAO.findByCustomerId(customer.getCustomerId());

        request.setAttribute("customer", customer);
        request.setAttribute("vehicles", vehicles);
        request.setAttribute("isSelfProfile", true);
        request.setAttribute("pageTitle", "Hồ sơ của tôi - CarCareBooking");
        request.getRequestDispatcher("/WEB-INF/views/customer/form.jsp").forward(request, response);
    }

    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        Customer customer = customerDAO.findByUserId(currentUser.getUserId());
        if (customer == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy thông tin hồ sơ khách hàng.");
            return;
        }

        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String address = request.getParameter("address");

        fullName = fullName != null ? fullName.trim() : "";
        phone = phone != null ? phone.trim() : "";
        email = email != null ? email.trim() : "";
        address = address != null ? address.trim() : "";

        // Kiểm tra hợp lệ phía Server
        StringBuilder errors = new StringBuilder();
        if (fullName.isEmpty() || fullName.length() < 2 || fullName.length() > 100) {
            errors.append("Họ và tên phải từ 2 đến 100 ký tự. ");
        }

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            errors.append("Số điện thoại không hợp lệ (phải gồm 10 chữ số, bắt đầu bằng số 0). ");
        } else {
            // Kiểm tra trùng SĐT với khách hàng khác
            Customer existingWithPhone = customerDAO.findByPhone(phone);
            if (existingWithPhone != null && existingWithPhone.getCustomerId() != customer.getCustomerId()) {
                errors.append("Số điện thoại này đã được sử dụng bởi khách hàng khác. ");
            }
        }

        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            errors.append("Định dạng email không hợp lệ. ");
        }

        if (errors.length() > 0) {
            customer.setFullName(fullName);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setAddress(address);

            List<Vehicle> vehicles = vehicleDAO.findByCustomerId(customer.getCustomerId());
            request.setAttribute("customer", customer);
            request.setAttribute("vehicles", vehicles);
            request.setAttribute("isSelfProfile", true);
            request.setAttribute("errorMessage", errors.toString());
            request.getRequestDispatcher("/WEB-INF/views/customer/form.jsp").forward(request, response);
            return;
        }

        boolean ok = customerDAO.updateProfile(customer.getCustomerId(), fullName, phone, email, address);
        if (ok) {
            // Cập nhật thông tin phiên đăng nhập
            currentUser.setFullName(fullName);
            currentUser.setPhone(phone);
            currentUser.setEmail(email);
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("currentUser", currentUser);
            }
            response.sendRedirect(request.getContextPath() + "/customers?action=profile&msg=" + urlEncode("Cập nhật hồ sơ thành công!"));
        } else {
            request.setAttribute("customer", customer);
            request.setAttribute("isSelfProfile", true);
            request.setAttribute("errorMessage", "Không thể lưu hồ sơ. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/WEB-INF/views/customer/form.jsp").forward(request, response);
        }
    }

    // ==========================================
    // CÁC HÀM XỬ LÝ NHÂN VIÊN (STAFF)
    // ==========================================

    private void handleStaffList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<Customer> customerList;
        if (keyword != null && !keyword.isBlank()) {
            customerList = customerDAO.search(keyword.trim());
        } else {
            customerList = customerDAO.findAll();
        }

        request.setAttribute("customerList", customerList);
        request.setAttribute("keyword", keyword);
        request.setAttribute("pageTitle", "Quản lý khách hàng - CarCareBooking");
        request.getRequestDispatcher("/WEB-INF/views/customer/list.jsp").forward(request, response);
    }

    private void handleStaffDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = parseInt(request.getParameter("id"), 0);
        Customer customer = customerDAO.findById(id);
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customers?action=list&error=" + urlEncode("Khách hàng không tồn tại."));
            return;
        }

        List<Vehicle> vehicles = vehicleDAO.findByCustomerId(customer.getCustomerId());
        request.setAttribute("customer", customer);
        request.setAttribute("vehicles", vehicles);
        request.setAttribute("isSelfProfile", false);
        request.setAttribute("pageTitle", "Chi tiết khách hàng: " + customer.getFullName());
        request.getRequestDispatcher("/WEB-INF/views/customer/form.jsp").forward(request, response);
    }

    private void handleStaffAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Customer customer = new Customer();
        customer.setMembershipLevel("STANDARD");
        request.setAttribute("customer", customer);
        request.setAttribute("isNew", true);
        request.setAttribute("isSelfProfile", false);
        request.setAttribute("pageTitle", "Thêm mới khách hàng - CarCareBooking");
        request.getRequestDispatcher("/WEB-INF/views/customer/form.jsp").forward(request, response);
    }

    private void handleStaffEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = parseInt(request.getParameter("id"), 0);
        Customer customer = customerDAO.findById(id);
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customers?action=list&error=" + urlEncode("Khách hàng không tồn tại."));
            return;
        }

        List<Vehicle> vehicles = vehicleDAO.findByCustomerId(customer.getCustomerId());
        request.setAttribute("customer", customer);
        request.setAttribute("vehicles", vehicles);
        request.setAttribute("isNew", false);
        request.setAttribute("isSelfProfile", false);
        request.setAttribute("pageTitle", "Chỉnh sửa khách hàng: " + customer.getFullName());
        request.getRequestDispatcher("/WEB-INF/views/customer/form.jsp").forward(request, response);
    }

    private void handleStaffSave(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String membershipLevel = request.getParameter("membershipLevel");

        fullName = fullName != null ? fullName.trim() : "";
        phone = phone != null ? phone.trim() : "";
        email = email != null ? email.trim() : "";
        address = address != null ? address.trim() : "";
        membershipLevel = membershipLevel != null ? membershipLevel.trim().toUpperCase() : "STANDARD";

        Customer customer = new Customer();
        customer.setFullName(fullName);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setAddress(address);
        customer.setMembershipLevel(membershipLevel);

        StringBuilder errors = new StringBuilder();
        if (fullName.isEmpty() || fullName.length() < 2) {
            errors.append("Họ tên không được để trống. ");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            errors.append("Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0). ");
        } else if (customerDAO.existsByPhone(phone, null)) {
            errors.append("Số điện thoại này đã được sử dụng. ");
        }
        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            errors.append("Email không đúng định dạng. ");
        }

        if (errors.length() > 0) {
            request.setAttribute("customer", customer);
            request.setAttribute("isNew", true);
            request.setAttribute("isSelfProfile", false);
            request.setAttribute("errorMessage", errors.toString());
            request.getRequestDispatcher("/WEB-INF/views/customer/form.jsp").forward(request, response);
            return;
        }

        int genId = customerDAO.insert(customer);
        if (genId > 0) {
            response.sendRedirect(request.getContextPath() + "/customers?action=detail&id=" + genId + "&msg=" + urlEncode("Thêm khách hàng thành công!"));
        } else {
            request.setAttribute("customer", customer);
            request.setAttribute("isNew", true);
            request.setAttribute("isSelfProfile", false);
            request.setAttribute("errorMessage", "Không thể thêm khách hàng. Vui lòng kiểm tra lại.");
            request.getRequestDispatcher("/WEB-INF/views/customer/form.jsp").forward(request, response);
        }
    }

    private void handleStaffUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int customerId = parseInt(request.getParameter("customerId"), 0);
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customers?action=list&error=" + urlEncode("Khách hàng không tồn tại."));
            return;
        }

        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String membershipLevel = request.getParameter("membershipLevel");

        fullName = fullName != null ? fullName.trim() : "";
        phone = phone != null ? phone.trim() : "";
        email = email != null ? email.trim() : "";
        address = address != null ? address.trim() : "";
        membershipLevel = membershipLevel != null ? membershipLevel.trim().toUpperCase() : "STANDARD";

        customer.setFullName(fullName);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setAddress(address);
        customer.setMembershipLevel(membershipLevel);

        StringBuilder errors = new StringBuilder();
        if (fullName.isEmpty() || fullName.length() < 2) {
            errors.append("Họ tên không được để trống. ");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            errors.append("Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0). ");
        } else if (customerDAO.existsByPhone(phone, customerId)) {
            errors.append("Số điện thoại này đã được sử dụng bởi khách hàng khác. ");
        }
        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            errors.append("Email không đúng định dạng. ");
        }

        if (errors.length() > 0) {
            List<Vehicle> vehicles = vehicleDAO.findByCustomerId(customer.getCustomerId());
            request.setAttribute("customer", customer);
            request.setAttribute("vehicles", vehicles);
            request.setAttribute("isSelfProfile", false);
            request.setAttribute("errorMessage", errors.toString());
            request.getRequestDispatcher("/WEB-INF/views/customer/form.jsp").forward(request, response);
            return;
        }

        boolean ok = customerDAO.update(customer);
        if (ok) {
            response.sendRedirect(request.getContextPath() + "/customers?action=detail&id=" + customerId + "&msg=" + urlEncode("Cập nhật thông tin khách hàng thành công!"));
        } else {
            List<Vehicle> vehicles = vehicleDAO.findByCustomerId(customer.getCustomerId());
            request.setAttribute("customer", customer);
            request.setAttribute("vehicles", vehicles);
            request.setAttribute("isSelfProfile", false);
            request.setAttribute("errorMessage", "Không thể cập nhật khách hàng.");
            request.getRequestDispatcher("/WEB-INF/views/customer/form.jsp").forward(request, response);
        }
    }

    private void handleStaffDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = parseInt(request.getParameter("id"), 0);
        boolean ok = customerDAO.delete(id);
        if (ok) {
            response.sendRedirect(request.getContextPath() + "/customers?action=list&msg=" + urlEncode("Đã xóa khách hàng thành công."));
        } else {
            response.sendRedirect(request.getContextPath() + "/customers?action=list&error=" + urlEncode("Không thể xóa khách hàng này do đã phát sinh xe hoặc giao dịch liên quan."));
        }
    }

    // ==========================================
    // TIỆN ÍCH TRỢ GIÚP (HELPERS)
    // ==========================================

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
