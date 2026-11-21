<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${pageTitle != null ? pageTitle : 'Quản lý phương tiện - CarCareBooking'}" />
<%@ include file="../layout/header.jspf" %>
<%@ include file="../layout/sidebar.jspf" %>

<main class="content-wrapper">
    <div class="container-fluid">
        <!-- Tiêu đề trang và nút hành động -->
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
            <div>
                <h3 class="fw-bold mb-1">
                    <c:choose>
                        <c:when test="${isCustomerView}">
                            <i class="bi bi-truck-front text-primary me-2"></i>Danh sách xe của tôi
                        </c:when>
                        <c:otherwise>
                            <i class="bi bi-car-front-fill text-primary me-2"></i>Quản lý phương tiện xe khách hàng
                        </c:otherwise>
                    </c:choose>
                </h3>
                <p class="text-muted small mb-0">
                    <c:choose>
                        <c:when test="${isCustomerView}">
                            Thông tin các phương tiện đã đăng ký để tiện đặt lịch hẹn chăm sóc, bảo dưỡng nhanh chóng.
                        </c:when>
                        <c:otherwise>
                            Tra cứu, thêm mới và quản lý thông tin xe của mọi khách hàng trong hệ thống xưởng.
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
            <div class="d-flex gap-2">
                <a href="${pageContext.request.contextPath}/vehicles?action=add${filterCustomerId > 0 ? ('&customerId=' + filterCustomerId) : ''}"
                   class="btn btn-primary btn-sm px-3">
                    <i class="bi bi-plus-circle-fill me-1"></i> Thêm xe mới
                </a>
                <c:choose>
                    <c:when test="${isCustomerView}">
                        <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-secondary btn-sm">
                            <i class="bi bi-arrow-left"></i> Dashboard
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/customers" class="btn btn-outline-secondary btn-sm">
                            <i class="bi bi-people me-1"></i> Danh sách KH
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Thông báo hệ thống -->
        <c:if test="${not empty param.msg}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i><c:out value="${param.msg}" />
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i><c:out value="${param.error}" />
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <!-- Bộ lọc và tìm kiếm cho nhân viên -->
        <c:if test="${!isCustomerView}">
            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body">
                    <form method="get" action="${pageContext.request.contextPath}/vehicles" class="row g-2 align-items-center">
                        <input type="hidden" name="action" value="list" />
                        <c:if test="${filterCustomerId > 0}">
                            <input type="hidden" name="customerId" value="${filterCustomerId}" />
                        </c:if>
                        <div class="col-md-9 col-sm-8">
                            <div class="input-group">
                                <span class="input-group-text bg-white"><i class="bi bi-search text-muted"></i></span>
                                <input type="text" class="form-control" name="keyword" value="<c:out value='${keyword}' />"
                                       placeholder="Tìm kiếm theo biển số xe, hãng xe, tên hoặc SĐT khách hàng..." />
                            </div>
                        </div>
                        <div class="col-md-3 col-sm-4 d-flex gap-2">
                            <button type="submit" class="btn btn-primary w-100">
                                <i class="bi bi-search me-1"></i> Tra cứu xe
                            </button>
                            <c:if test="${not empty keyword || filterCustomerId > 0}">
                                <a href="${pageContext.request.contextPath}/vehicles" class="btn btn-outline-secondary" title="Xóa bộ lọc">
                                    <i class="bi bi-x-lg"></i>
                                </a>
                            </c:if>
                        </div>
                    </form>
                    <c:if test="${filterCustomer != null}">
                        <div class="mt-2 pt-2 border-top small text-muted">
                            Đang lọc phương tiện của khách hàng: <strong><c:out value="${filterCustomer.fullName}" /></strong>
                            (${filterCustomer.phone})
                            <a href="${pageContext.request.contextPath}/vehicles" class="badge bg-secondary-subtle text-secondary ms-2 text-decoration-none">
                                Xem tất cả xe
                            </a>
                        </div>
                    </c:if>
                </div>
            </div>
        </c:if>

        <!-- Danh sách xe -->
        <div class="card border-0 shadow-sm">
            <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                <span class="fw-semibold text-secondary">
                    Tổng số phương tiện: <strong class="text-primary">${vehicleList != null ? vehicleList.size() : 0}</strong>
                </span>
                <c:if test="${isCustomerView}">
                    <a href="${pageContext.request.contextPath}/bookings" class="btn btn-sm btn-outline-primary">
                        <i class="bi bi-calendar2-plus me-1"></i> Đặt lịch hẹn ngay
                    </a>
                </c:if>
            </div>
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th scope="col" style="width: 130px;">Biển số</th>
                            <th scope="col">Hãng xe & Dòng xe</th>
                            <th scope="col" class="text-center">Phân loại</th>
                            <th scope="col" class="text-center">Năm SX</th>
                            <th scope="col" class="text-center">Màu sắc</th>
                            <c:if test="${!isCustomerView}">
                                <th scope="col">Chủ sở hữu</th>
                            </c:if>
                            <th scope="col" class="text-end" style="width: 150px;">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty vehicleList}">
                                <c:forEach var="v" items="${vehicleList}">
                                    <tr>
                                        <td>
                                            <span class="badge bg-dark-subtle text-dark border font-monospace fs-6 px-2 py-1">
                                                <c:out value="${v.licensePlate}" />
                                            </span>
                                        </td>
                                        <td>
                                            <div class="fw-bold text-dark"><c:out value="${v.brand}" /> <c:out value="${v.model}" /></div>
                                        </td>
                                        <td class="text-center">
                                            <span class="badge ${v.typeBadgeClass} px-2 py-1">
                                                <c:out value="${v.vehicleType}" />
                                            </span>
                                        </td>
                                        <td class="text-center">
                                            <c:out value="${v.year != null && v.year > 0 ? v.year : '-'}" />
                                        </td>
                                        <td class="text-center">
                                            <c:out value="${v.color != null && !v.color.isBlank() ? v.color : '-'}" />
                                        </td>
                                        <c:if test="${!isCustomerView}">
                                            <td>
                                                <div class="fw-semibold text-dark"><c:out value="${v.customerName}" /></div>
                                                <small class="text-muted"><i class="bi bi-telephone me-1"></i><c:out value="${v.customerPhone}" /></small>
                                            </td>
                                        </c:if>
                                        <td class="text-end">
                                            <div class="btn-group btn-group-sm">
                                                <a href="${pageContext.request.contextPath}/vehicles?action=edit&id=${v.vehicleId}"
                                                   class="btn btn-outline-secondary" title="Sửa thông tin xe">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/vehicles?action=delete&id=${v.vehicleId}"
                                                   class="btn btn-outline-danger" title="Xóa xe"
                                                   onclick="return confirm('Bạn có chắc chắn muốn xóa phương tiện [${v.licensePlate}]?');">
                                                    <i class="bi bi-trash"></i>
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="${isCustomerView ? 6 : 7}" class="text-center py-5 text-muted">
                                        <i class="bi bi-car-front fs-1 d-block mb-2 text-secondary"></i>
                                        <c:choose>
                                            <c:when test="${isCustomerView}">
                                                Bạn chưa đăng ký phương tiện xe nào. Vui lòng bấm <strong>Thêm xe mới</strong> để tiện lợi khi đặt lịch!
                                            </c:when>
                                            <c:otherwise>
                                                Không tìm thấy thông tin phương tiện nào phù hợp với điều kiện tìm kiếm.
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</main>

<%@ include file="../layout/footer.jspf" %>
