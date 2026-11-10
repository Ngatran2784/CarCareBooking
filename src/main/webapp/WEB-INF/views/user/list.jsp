<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Quản lý Người dùng - CarCareBooking" />
<%@ include file="../layout/header.jspf" %>
<%@ include file="../layout/sidebar.jspf" %>

<main class="content-wrapper">
    <div class="container-fluid">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <div>
                <h3 class="fw-bold mb-1">Danh sách người dùng hệ thống</h3>
                <p class="text-secondary mb-0">Quản lý tài khoản đăng nhập và phân quyền vai trò vận hành</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-secondary btn-sm">
                    <i class="bi bi-arrow-left"></i> Bảng điều khiển
                </a>
            </div>
        </div>

        <div class="card shadow-sm">
            <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                <span class="fw-semibold"><i class="bi bi-people me-2 text-primary"></i>Danh sách tài khoản (<c:out value="${users.size()}" /> tài khoản)</span>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th style="width: 60px;">ID</th>
                                <th>Tên đăng nhập</th>
                                <th>Họ và tên</th>
                                <th>Email</th>
                                <th>Số điện thoại</th>
                                <th>Vai trò chính</th>
                                <th>Trạng thái</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty users}">
                                    <c:forEach var="u" items="${users}">
                                        <tr>
                                            <td class="text-muted fw-bold">#<c:out value="${u.userId}" /></td>
                                            <td>
                                                <div class="fw-semibold text-dark"><c:out value="${u.username}" /></div>
                                            </td>
                                            <td><c:out value="${u.fullName}" /></td>
                                            <td><c:out value="${u.email}" /></td>
                                            <td><c:out value="${u.phone}" /></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${u.role.roleName == 'ADMIN'}">
                                                        <span class="badge bg-danger-subtle text-danger border border-danger-subtle">ADMIN</span>
                                                    </c:when>
                                                    <c:when test="${u.role.roleName == 'MANAGER'}">
                                                        <span class="badge bg-primary-subtle text-primary border border-primary-subtle">MANAGER</span>
                                                    </c:when>
                                                    <c:when test="${u.role.roleName == 'RECEPTIONIST'}">
                                                        <span class="badge bg-info-subtle text-info border border-info-subtle">RECEPTIONIST</span>
                                                    </c:when>
                                                    <c:when test="${u.role.roleName == 'TECHNICIAN'}">
                                                        <span class="badge bg-warning-subtle text-warning border border-warning-subtle">TECHNICIAN</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-success-subtle text-success border border-success-subtle">CUSTOMER</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${u.status == 'ACTIVE'}">
                                                        <span class="badge bg-success"><i class="bi bi-check-circle me-1"></i>Hoạt động</span>
                                                    </c:when>
                                                    <c:when test="${u.status == 'LOCKED'}">
                                                        <span class="badge bg-danger"><i class="bi bi-lock me-1"></i>Đã khóa</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">Tạm ngưng</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="7" class="text-center py-4 text-muted">
                                            Chưa có tài khoản người dùng nào trong cơ sở dữ liệu.
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</main>

<%@ include file="../layout/footer.jspf" %>
