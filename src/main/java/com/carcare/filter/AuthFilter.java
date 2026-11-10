package com.carcare.filter;

import com.carcare.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * AuthFilter: Bộ lọc kiểm tra xác thực danh tính và phân quyền vai trò người dùng (RBAC).
 * Đảm bảo chỉ người dùng đã đăng nhập và có vai trò phù hợp mới được truy cập các tài nguyên nghiệp vụ.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        // 1. Bỏ qua kiểm tra đối với các tài nguyên tĩnh công khai (CSS, JS, Fonts, Images)
        if (isStaticResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Bỏ qua kiểm tra đối với trang xác thực (Login, Logout, Register) và trang điều hướng gốc
        if (isPublicAuthPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 3. Kiểm tra trạng thái phiên đăng nhập (Authentication)
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            // Chưa đăng nhập -> Lưu URL đang truy cập (nếu hợp lệ) và chuyển hướng về trang đăng nhập
            if (!"/".equals(path) && !"/index.jsp".equals(path)) {
                if (session == null) {
                    session = req.getSession(true);
                }
                session.setAttribute("targetUrl", path);
            }
            res.sendRedirect(contextPath + "/auth?action=login");
            return;
        }

        // Kiểm tra xem tài khoản có bị khóa trong khi phiên còn hoạt động không
        if (currentUser.isLocked()) {
            session.invalidate();
            res.sendRedirect(contextPath + "/auth?action=login&error=locked");
            return;
        }

        // 4. Kiểm tra phân quyền theo vai trò (Role-Based Authorization)
        if (!hasPermission(currentUser, path)) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            req.getRequestDispatcher("/WEB-INF/views/error/403.jsp").forward(req, res);
            return;
        }

        // Đủ thẩm quyền -> Tiếp tục xử lý request
        chain.doFilter(request, response);
    }

    /**
     * Xác định xem đường dẫn có thuộc tài nguyên tĩnh công khai không.
     */
    private boolean isStaticResource(String path) {
        if (path.startsWith("/assets/")) {
            return true;
        }
        String lower = path.toLowerCase();
        return lower.endsWith(".css") || lower.endsWith(".js")
                || lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".gif") || lower.endsWith(".svg") || lower.endsWith(".ico")
                || lower.endsWith(".woff") || lower.endsWith(".woff2") || lower.endsWith(".ttf");
    }

    /**
     * Xác định xem đường dẫn có thuộc trang xác thực công khai không.
     */
    private boolean isPublicAuthPath(String path) {
        return path.equals("/") || path.equals("/index.jsp")
                || path.equals("/auth") || path.startsWith("/auth/")
                || path.equals("/login") || path.equals("/logout");
    }

    /**
     * Kiểm tra quyền truy cập của người dùng đối với từng cụm URL nghiệp vụ.
     */
    private boolean hasPermission(User user, String path) {
        // Cụm quản trị và báo cáo hệ thống: Chỉ ADMIN và MANAGER
        if (path.startsWith("/users") || path.startsWith("/reports") || path.startsWith("/staff") || path.startsWith("/bays")) {
            return user.hasRole("ADMIN") || user.hasRole("MANAGER");
        }

        // Cụm quản lý danh mục và điều phối xưởng: ADMIN, MANAGER, RECEPTIONIST
        if (path.startsWith("/customers") || path.startsWith("/vehicles") || path.startsWith("/services")
                || path.startsWith("/dispatch") || path.startsWith("/checkin")
                || path.startsWith("/invoices") || path.startsWith("/payments")) {
            return user.hasRole("ADMIN") || user.hasRole("MANAGER") || user.hasRole("RECEPTIONIST");
        }

        // Cụm công việc kỹ thuật xưởng: ADMIN, MANAGER, RECEPTIONIST, TECHNICIAN
        if (path.startsWith("/jobs") || path.startsWith("/inspections")
                || path.startsWith("/additional-services") || path.startsWith("/quality-checks")) {
            return user.hasRole("ADMIN") || user.hasRole("MANAGER")
                    || user.hasRole("RECEPTIONIST") || user.hasRole("TECHNICIAN");
        }

        // Các trang chung (Dashboard /home, Đặt lịch /bookings, Lịch sử /booking-history, Đánh giá /reviews, Thông báo /notifications):
        // Mọi người dùng đã đăng nhập hợp lệ đều được phép truy cập
        return true;
    }

    @Override
    public void destroy() {}
}
