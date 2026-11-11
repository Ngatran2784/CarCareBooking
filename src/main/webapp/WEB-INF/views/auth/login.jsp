<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Đăng nhập hệ thống - CarCareBooking" />
<%@ include file="../layout/header.jspf" %>
<%@ include file="../layout/sidebar.jspf" %>

<main class="content-wrapper">
    <div class="container-fluid">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <div>
                <h3 class="fw-bold mb-1">Đăng nhập hệ thống</h3>
                <span class="badge bg-primary">Sprint 1</span>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-secondary btn-sm">
                    <i class="bi bi-arrow-left"></i> Quay lại Dashboard
                </a>
            </div>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger" role="alert">
                <c:out value="${errorMessage}" />
            </div>
        </c:if>

        <div class="card">
            <div class="card-header bg-white">
                <i class="bi bi-person-lock me-2"></i>Thông tin đăng nhập
            </div>
            <div class="card-body">
                <form method="post" action="${pageContext.request.contextPath}/auth">
                    <div class="mb-3">
                        <label for="usernameOrEmail" class="form-label">Tên đăng nhập hoặc email</label>
                        <input type="text" class="form-control" id="usernameOrEmail" name="usernameOrEmail"
                               value="<c:out value='${identifier}' />" autocomplete="username" required autofocus>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Mật khẩu</label>
                        <input type="password" class="form-control" id="password" name="password"
                               autocomplete="current-password" required>
                    </div>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-box-arrow-in-right me-1"></i>Đăng nhập
                    </button>
                </form>
            </div>
        </div>
    </div>
</main>

<%@ include file="../layout/footer.jspf" %>
