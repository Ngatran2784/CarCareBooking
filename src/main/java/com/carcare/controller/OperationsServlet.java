package com.carcare.controller;

import com.carcare.dal.OperationsDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.util.*;

@WebServlet(name="OperationsServlet", urlPatterns={"/staff","/settings"})
public class OperationsServlet extends HttpServlet {
    private final OperationsDAO dao=new OperationsDAO();
    private boolean staff(HttpServletRequest r) { return "/staff".equals(r.getServletPath()); }
    @Override protected void doGet(HttpServletRequest r,HttpServletResponse s) throws ServletException,IOException {
        if(r.getSession().getAttribute("operationsCsrf")==null) r.getSession().setAttribute("operationsCsrf",UUID.randomUUID().toString());
        Object flash=r.getSession().getAttribute("operationsSuccess");
        r.getSession().removeAttribute("operationsSuccess"); r.setAttribute("success",flash);
        try {
            r.setAttribute("settings",dao.settings());
            for(String type:staff(r)?List.of("technicians","services","shifts","skills"):List.of("holidays")) r.setAttribute(type,dao.list(type));
            r.setAttribute("ready",true);
        } catch(SQLException e) {
            log("Cannot load operations",e); s.setStatus(503);
            r.setAttribute("error","Không thể tải dữ liệu. Kiểm tra kết nối CSDL và chạy migration 004 nếu chưa có cấu hình vận hành.");
        }
        r.getRequestDispatcher(staff(r)?"/WEB-INF/views/staff/list.jsp":"/WEB-INF/views/settings/index.jsp").forward(r,s);
    }
    @Override protected void doPost(HttpServletRequest r,HttpServletResponse s) throws ServletException,IOException {
        Object token=r.getSession().getAttribute("operationsCsrf");
        if(token==null || !token.equals(r.getParameter("csrf"))) { s.sendError(403,"Phiên biểu mẫu không hợp lệ. Vui lòng tải lại trang."); return; }
        Map<String,String> f=new HashMap<>();
        r.getParameterMap().forEach((k,v)->f.put(k,v[0]));
        try {
            dao.save(f,staff(r));
            r.getSession().setAttribute("operationsSuccess","Đã lưu thay đổi thành công.");
            s.sendRedirect(r.getContextPath()+r.getServletPath());
        } catch(DateTimeException e) {
            r.setAttribute("error","Ngày hoặc giờ không hợp lệ."); r.setAttribute("form",f); s.setStatus(400); doGet(r,s);
        } catch(IllegalArgumentException e) {
            r.setAttribute("error",e.getMessage()); r.setAttribute("form",f); s.setStatus(400); doGet(r,s);
        } catch(SQLException e) {
            log("Cannot save operations",e); r.setAttribute("error","Không lưu được dữ liệu. Vui lòng kiểm tra CSDL và thử lại."); r.setAttribute("form",f); s.setStatus(503); doGet(r,s);
        }
    }
}
