<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="403 - Không có quyền truy cập" />
<%@ include file="../layout/header.jspf" %>
<%@ include file="../layout/sidebar.jspf" %>

<main class="content-wrapper">
    <div class="container-fluid py-5 text-center">
        <div class="card p-5 mx-auto shadow-sm" style="max-width: 600px;">
            <div class="mb-3">
                <i class="bi bi-shield-lock-fill text-danger" style="font-size: 4rem;"></i>
            </div>
            <h2 class="fw-bold text-danger mb-2">403 - Quyền truy cập bị từ chối</h2>
            <p class="text-secondary mb-4">
                Tài khoản <strong>${sessionScope.currentUser != null ? sessionScope.currentUser.fullName : "khách"}</strong> 
                (Vai trò: <code>${sessionScope.currentUser != null ? sessionScope.currentUser.primaryRoleName : "Chưa đăng nhập"}</code>) 
                không có đủ thẩm quyền để truy cập vào địa chỉ này.
            </p>
            <div class="d-flex justify-content-center gap-2">
                <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">
                    <i class="bi bi-house-door me-1"></i> Quay lại Trang chủ
                </a>
                <a href="${pageContext.request.contextPath}/auth?action=logout" class="btn btn-outline-danger">
                    <i class="bi bi-box-arrow-right me-1"></i> Đăng xuất & Đổi tài khoản
                </a>
            </div>
        </div>
    </div>
</main>

<%@ include file="../layout/footer.jspf" %>
