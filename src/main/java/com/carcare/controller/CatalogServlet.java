package com.carcare.controller;

import com.carcare.dal.CatalogDAO;
import com.carcare.model.User;
import com.carcare.service.CatalogRules;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.util.*;

@WebServlet(name="CatalogServlet", urlPatterns={"/services", "/bays"})
public class CatalogServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CatalogDAO dao = new CatalogDAO();
    private boolean services(HttpServletRequest r) { return "/services".equals(r.getServletPath()); }
    private boolean canManage(HttpServletRequest r) {
        User user = (User)r.getSession().getAttribute("currentUser");
        return user != null && (user.hasRole("ADMIN") || user.hasRole("MANAGER"));
    }
    @Override protected void doGet(HttpServletRequest r, HttpServletResponse s) throws ServletException, IOException {
        boolean services = services(r);
        if (!canManage(r) && (r.getParameter("edit") != null || r.getParameter("maintenanceEdit") != null)) { s.sendError(403); return; }
        if (r.getSession().getAttribute("catalogCsrf") == null)
            r.getSession().setAttribute("catalogCsrf", UUID.randomUUID().toString());
        Object success = r.getSession().getAttribute("catalogSuccess");
        r.getSession().removeAttribute("catalogSuccess");
        r.setAttribute("success", success);
        try {
            List<Map<String,Object>> items = dao.list(services ? "services" : "bays");
            r.setAttribute("items", items);
            r.setAttribute("details", dao.list(services ? "prices" : "maintenance"));
            if (r.getAttribute("form") == null && r.getParameter("edit") != null)
                r.setAttribute("form", edit(items, CatalogRules.id(r.getParameter("edit")), services ? "service_id" : "bay_id"));
            if (!services && r.getAttribute("maintenanceForm") == null && r.getParameter("maintenanceEdit") != null) {
                @SuppressWarnings("unchecked") List<Map<String,Object>> details = (List<Map<String,Object>>)r.getAttribute("details");
                r.setAttribute("maintenanceForm", edit(details, CatalogRules.id(r.getParameter("maintenanceEdit")), "maintenance_id"));
            }
            r.setAttribute("ready", true);
        } catch (IllegalArgumentException e) { s.setStatus(400); r.setAttribute("error", e.getMessage()); }
        catch (SQLException e) {
            log("Cannot load catalogue", e); s.setStatus(503);
            r.setAttribute("error", "Không tải được dữ liệu. Kiểm tra CSDL và các migration 004, 005.");
        }
        r.getRequestDispatcher(services ? "/WEB-INF/views/service/list.jsp" : "/WEB-INF/views/bay/list.jsp").forward(r,s);
    }
    private Map<String,String> edit(List<Map<String,Object>> rows, int id, String key) {
        for (Map<String,Object> row : rows) if (((Number)row.get(key)).intValue() == id) {
            Map<String,String> f = new HashMap<>();
            row.forEach((k,v) -> f.put(k, v == null ? "" : v.toString()));
            f.put("id", "" + id);
            if (f.containsKey("start_time")) {
                f.put("start_time", row.get("start_time") instanceof java.sql.Timestamp t ? t.toLocalDateTime().toString() : f.get("start_time").replace(' ', 'T'));
                f.put("end_time", row.get("end_time") instanceof java.sql.Timestamp t ? t.toLocalDateTime().toString() : f.get("end_time").replace(' ', 'T'));
            }
            return f;
        }
        throw new IllegalArgumentException("Dữ liệu cần sửa không tồn tại.");
    }
    @Override protected void doPost(HttpServletRequest r, HttpServletResponse s) throws ServletException, IOException {
        if (!canManage(r)) { s.sendError(403); return; }
        Object token = r.getSession().getAttribute("catalogCsrf");
        if (token == null || !token.equals(r.getParameter("csrf"))) { s.sendError(403, "Vui lòng tải lại biểu mẫu."); return; }
        Map<String,String> f = new HashMap<>();
        r.getParameterMap().forEach((k,v) -> f.put(k,v[0]));
        try {
            dao.save(f, services(r), ((User)r.getSession().getAttribute("currentUser")).getUserId());
            r.getSession().setAttribute("catalogSuccess", "Đã lưu thay đổi thành công.");
            s.sendRedirect(r.getContextPath() + r.getServletPath());
            return;
        } catch (DateTimeException e) { s.setStatus(400); r.setAttribute("error", "Ngày giờ bảo trì không hợp lệ."); }
        catch (IllegalArgumentException e) { s.setStatus(400); r.setAttribute("error", e.getMessage()); }
        catch (SQLException e) {
            log("Cannot save catalogue", e);
            s.setStatus(e.getSQLState() != null && e.getSQLState().startsWith("23") ? 400 : 503);
            r.setAttribute("error", e.getSQLState() != null && e.getSQLState().startsWith("23")
                ? "Dữ liệu đang được sử dụng hoặc bị trùng. Vui lòng tải lại trang và kiểm tra."
                : "Không lưu được dữ liệu. Vui lòng kiểm tra kết nối CSDL và thử lại.");
        }
        r.setAttribute("maintenance".equals(f.get("action")) ? "maintenanceForm" : "form", f);
        doGet(r,s);
    }
}
