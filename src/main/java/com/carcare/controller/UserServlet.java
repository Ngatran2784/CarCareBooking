package com.carcare.controller;

import com.carcare.dal.UserDAO;
import com.carcare.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller: UserServlet
 * Quản lý danh sách tài khoản người dùng và phân quyền vai trò.
 * Mapping: /users
 */
@WebServlet(name = "UserServlet", urlPatterns = {"/users"})
public class UserServlet extends HttpServlet {
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

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        if ("form".equalsIgnoreCase(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr);
                    User user = userDAO.findById(id);
                    request.setAttribute("user", user);
                } catch (NumberFormatException ignored) {}
            }
            request.getRequestDispatcher("/WEB-INF/views/user/form.jsp").forward(request, response);
            return;
        }

        // Mặc định: Hiển thị danh sách người dùng thực tế từ CSDL
        List<User> userList = userDAO.findAll();
        request.setAttribute("users", userList);
        request.getRequestDispatcher("/WEB-INF/views/user/list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
