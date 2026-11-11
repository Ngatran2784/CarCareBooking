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
import java.sql.SQLException;

/**
 * Controller: AuthServlet
 * Quản lý Đăng nhập, Đăng xuất & Đăng ký (Sprint 1)
 * Mapping: /auth
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/auth"})
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        showLogin(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String identifier = request.getParameter("usernameOrEmail");
        String password = request.getParameter("password");
        if (identifier == null || identifier.isBlank() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập tên đăng nhập/email và mật khẩu.");
            request.setAttribute("identifier", identifier);
            showLogin(request, response);
            return;
        }

        try {
            User user = userDAO.login(identifier, password);
            if (user == null) {
                request.setAttribute("errorMessage", "Tên đăng nhập/email hoặc mật khẩu không đúng, hoặc tài khoản chưa được kích hoạt.");
                request.setAttribute("identifier", identifier);
                showLogin(request, response);
                return;
            }

            HttpSession previousSession = request.getSession(false);
            if (previousSession != null) {
                previousSession.invalidate();
            }
            request.getSession(true).setAttribute("currentUser", user);
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (SQLException e) {
            getServletContext().log("Không thể xác thực tài khoản do lỗi cơ sở dữ liệu.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("errorMessage", "Hiện không thể đăng nhập. Vui lòng thử lại sau.");
            request.setAttribute("identifier", identifier);
            showLogin(request, response);
        }
    }

    private void showLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("moduleName", "Auth");
        request.setAttribute("action", "login");
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }
}
