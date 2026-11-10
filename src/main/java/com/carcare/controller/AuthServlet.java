package com.carcare.controller;

import com.carcare.dal.UserDAO;
import com.carcare.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controller: AuthServlet
 * Xử lý xác thực người dùng: Đăng nhập, Đăng xuất và điều hướng phân quyền.
 * URL Patterns: /auth, /login, /logout
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/auth", "/login", "/logout"})
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String action = request.getParameter("action");

        // 1. Xử lý Đăng xuất (Logout)
        if ("/logout".equals(path) || "logout".equalsIgnoreCase(action)) {
            handleLogout(request, response);
            return;
        }

        // 2. Nếu đã đăng nhập thì tự động chuyển hướng vào Trang chủ
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        // 3. Xử lý mở giao diện Đăng ký (Register)
        if ("register".equalsIgnoreCase(action)) {
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        // 4. Mặc định mở giao diện Đăng nhập (Login)
        if ("locked".equalsIgnoreCase(request.getParameter("error"))) {
            request.setAttribute("errorMessage", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");
        }
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String action = request.getParameter("action");

        if ("/logout".equals(path) || "logout".equalsIgnoreCase(action)) {
            handleLogout(request, response);
            return;
        }

        // Xử lý xác thực thông tin đăng nhập
        handleLogin(request, response);
    }

    /**
     * Quy trình xác thực đăng nhập an toàn:
     * - Kiểm tra dữ liệu đầu vào
     * - Xác thực mật khẩu qua BCrypt
     * - Kiểm tra trạng thái khóa (LOCKED) / kích hoạt (ACTIVE)
     * - Chống session fixation (làm mới session ID)
     * - Lưu thông tin định danh vào session và chuyển hướng đúng vai trò
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usernameOrEmail = request.getParameter("username");
        String password = request.getParameter("password");

        // Validate đầu vào
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            request.setAttribute("username", usernameOrEmail != null ? usernameOrEmail.trim() : "");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        usernameOrEmail = usernameOrEmail.trim();

        // Kiểm tra từ cơ sở dữ liệu qua UserDAO
        User user = userDAO.authenticate(usernameOrEmail, password);

        if (user == null) {
            // Thông báo lỗi chung, bảo mật danh tính (không tiết lộ tên đăng nhập tồn tại hay không)
            request.setAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không chính xác.");
            request.setAttribute("username", usernameOrEmail);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        // Kiểm tra trạng thái tài khoản
        if (user.isLocked()) {
            request.setAttribute("errorMessage", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");
            request.setAttribute("username", usernameOrEmail);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        if (!user.isActive()) {
            request.setAttribute("errorMessage", "Tài khoản chưa được kích hoạt hoặc đã tạm ngưng hoạt động.");
            request.setAttribute("username", usernameOrEmail);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        // Chống Session Fixation: Hủy session cũ và tạo session mới
        HttpSession oldSession = request.getSession(false);
        String targetUrl = null;
        if (oldSession != null) {
            targetUrl = (String) oldSession.getAttribute("targetUrl");
            oldSession.invalidate();
        }

        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("currentUser", user);
        newSession.setAttribute("userId", user.getUserId());
        newSession.setAttribute("username", user.getUsername());
        newSession.setAttribute("fullName", user.getFullName());
        newSession.setAttribute("userRole", user.getPrimaryRoleName());

        // Điều hướng người dùng tới trang trước đó nếu có, hoặc vào trang chủ Dashboard
        if (targetUrl != null && !targetUrl.isBlank() && !targetUrl.contains("login") && !targetUrl.contains("auth")) {
            response.sendRedirect(request.getContextPath() + targetUrl);
        } else {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    /**
     * Quy trình đăng xuất an toàn: Hủy session và chuyển về trang đăng nhập
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/auth?action=login&logout=success");
    }
}
