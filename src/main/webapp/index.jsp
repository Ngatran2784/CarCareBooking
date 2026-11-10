<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Chuyển hướng người dùng sang HomeServlet (/home)
    response.sendRedirect(request.getContextPath() + "/home");
%>
