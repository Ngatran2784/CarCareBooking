package com.carcare.controller;

import com.carcare.dal.PasswordResetDAO;
import com.carcare.dal.UserDAO;
import com.carcare.model.User;
import com.carcare.util.ValidationUtil;
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

    private static final String REGISTER_VIEW = "/WEB-INF/views/auth/register.jsp";
    private static final String FORGOT_VIEW = "/WEB-INF/views/auth/forgot-password.jsp";
    private static final String RESET_VIEW = "/WEB-INF/views/auth/reset-password.jsp";

    private UserDAO userDAO;
    private PasswordResetDAO passwordResetDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userDAO = new UserDAO();
        this.passwordResetDAO = new PasswordResetDAO();
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
            request.getRequestDispatcher(REGISTER_VIEW).forward(request, response);
            return;
        }

        // 3b. Quên mật khẩu: mở biểu mẫu yêu cầu đặt lại
        if ("forgot".equalsIgnoreCase(action)) {
            request.getRequestDispatcher(FORGOT_VIEW).forward(request, response);
            return;
        }

        // 3c. Mở biểu mẫu đặt mật khẩu mới từ liên kết có token
        if ("reset".equalsIgnoreCase(action)) {
            String token = request.getParameter("token");
            if (!passwordResetDAO.isTokenValid(token)) {
                request.setAttribute("tokenInvalid", true);
            } else {
                request.setAttribute("token", token);
            }
            request.getRequestDispatcher(RESET_VIEW).forward(request, response);
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

        if ("register".equalsIgnoreCase(action)) {
            handleRegister(request, response);
            return;
        }
        if ("forgot".equalsIgnoreCase(action)) {
            handleForgotPassword(request, response);
            return;
        }
        if ("reset".equalsIgnoreCase(action)) {
            handleResetPassword(request, response);
            return;
        }

        // Xử lý xác thực thông tin đăng nhập
        handleLogin(request, response);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    /**
     * Đăng ký tài khoản khách hàng: kiểm tra dữ liệu, kiểm tra trùng, tạo users + customers.
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = trim(request.getParameter("fullName")).replaceAll("\\s+", " ");
        String username = trim(request.getParameter("username"));
        String phone = trim(request.getParameter("phone"));
        String email = trim(request.getParameter("email"));
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirmPassword");

        java.util.Map<String, String> errors = new java.util.LinkedHashMap<>();
        if (fullName.length() < 2 || fullName.length() > 100) {
            errors.put("fullName", "Họ và tên phải từ 2 đến 100 ký tự.");
        }
        if (!ValidationUtil.isValidUsername(username)) {
            errors.put("username", "Tên đăng nhập 4-30 ký tự, chỉ gồm chữ, số, dấu chấm hoặc gạch dưới.");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            errors.put("phone", "Số điện thoại không hợp lệ (bắt đầu bằng 0, gồm 10-11 chữ số).");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            errors.put("email", "Địa chỉ email không hợp lệ.");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            errors.put("password", "Mật khẩu tối thiểu 8 ký tự, gồm ít nhất một chữ cái và một chữ số.");
        } else if (!password.equals(confirm)) {
            errors.put("confirmPassword", "Mật khẩu xác nhận không khớp.");
        }

        if (errors.isEmpty()) {
            if (userDAO.exists("username", username)) errors.put("username", "Tên đăng nhập đã được sử dụng.");
            if (userDAO.exists("email", email)) errors.put("email", "Email đã được đăng ký.");
            if (userDAO.exists("phone", phone)) errors.put("phone", "Số điện thoại đã được đăng ký.");
        }

        if (errors.isEmpty()) {
            int result = userDAO.registerCustomer(username, password, fullName, email, phone, null);
            if (result > 0) {
                response.sendRedirect(request.getContextPath() + "/auth?action=login&registered=success");
                return;
            }
            if (result == UserDAO.REGISTER_DUPLICATE) {
                errors.put("phone", "Thông tin đã tồn tại trong hệ thống. Vui lòng liên hệ cửa hàng nếu bạn từng sử dụng dịch vụ.");
            } else {
                request.setAttribute("errorMessage", "Không thể tạo tài khoản lúc này. Vui lòng thử lại sau.");
            }
        }

        request.setAttribute("errors", errors);
        request.setAttribute("fullName", fullName);
        request.setAttribute("username", username);
        request.setAttribute("phone", phone);
        request.setAttribute("email", email);
        request.getRequestDispatcher(REGISTER_VIEW).forward(request, response);
    }

    /**
     * Yêu cầu đặt lại mật khẩu. Luôn trả về cùng một thông báo để không lộ email có tồn tại hay không.
     * Hệ thống chưa cấu hình gửi email: liên kết được ghi vào log máy chủ, và chỉ hiển thị
     * trực tiếp trên trang khi yêu cầu đến từ chính máy chủ (localhost - môi trường phát triển).
     */
    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = trim(request.getParameter("email"));
        if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("errorMessage", "Vui lòng nhập địa chỉ email hợp lệ.");
            request.setAttribute("email", email);
            request.getRequestDispatcher(FORGOT_VIEW).forward(request, response);
            return;
        }

        User user = userDAO.findActiveByEmail(email);
        if (user != null) {
            String token = passwordResetDAO.createToken(user.getUserId());
            if (token != null) {
                String link = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                        + request.getContextPath() + "/auth?action=reset&token=" + token;
                System.out.println("[PasswordReset] Lien ket dat lai mat khau cho " + user.getUsername() + ": " + link);
                String remote = request.getRemoteAddr();
                boolean local = "127.0.0.1".equals(remote) || "0:0:0:0:0:0:0:1".equals(remote) || "::1".equals(remote);
                if (local) {
                    request.setAttribute("devResetLink", link);
                }
            }
        }
        request.setAttribute("sent", true);
        request.setAttribute("ttlMinutes", PasswordResetDAO.TOKEN_TTL_MINUTES);
        request.getRequestDispatcher(FORGOT_VIEW).forward(request, response);
    }

    /**
     * Đặt mật khẩu mới bằng token dùng một lần.
     */
    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirmPassword");

        if (!passwordResetDAO.isTokenValid(token)) {
            request.setAttribute("tokenInvalid", true);
            request.getRequestDispatcher(RESET_VIEW).forward(request, response);
            return;
        }
        if (!ValidationUtil.isValidPassword(password)) {
            request.setAttribute("errorMessage", "Mật khẩu tối thiểu 8 ký tự, gồm ít nhất một chữ cái và một chữ số.");
        } else if (!password.equals(confirm)) {
            request.setAttribute("errorMessage", "Mật khẩu xác nhận không khớp.");
        } else if (passwordResetDAO.resetPassword(token, password)) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login&reset=success");
            return;
        } else {
            request.setAttribute("tokenInvalid", true);
            request.getRequestDispatcher(RESET_VIEW).forward(request, response);
            return;
        }
        request.setAttribute("token", token);
        request.getRequestDispatcher(RESET_VIEW).forward(request, response);
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
