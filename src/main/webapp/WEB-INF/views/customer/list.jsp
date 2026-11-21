<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Quản lý khách hàng - CarCareBooking" />
<%@ include file="../layout/header.jspf" %>
<%@ include file="../layout/sidebar.jspf" %>

<main class="content-wrapper">
    <div class="container-fluid">
        <!-- Tiêu đề và nút tác vụ -->
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
            <div>
                <h3 class="fw-bold mb-1"><i class="bi bi-people text-primary me-2"></i>Quản lý danh sách khách hàng</h3>
                <p class="text-muted small mb-0">Tra cứu hồ sơ khách hàng, số điện thoại, hạng thẻ và danh sách xe đã đăng ký.</p>
            </div>
            <div class="d-flex gap-2">
                <a href="${pageContext.request.contextPath}/customers?action=add" class="btn btn-primary btn-sm px-3">
                    <i class="bi bi-person-plus-fill me-1"></i> Thêm khách hàng mới
                </a>
                <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-secondary btn-sm">
                    <i class="bi bi-arrow-left"></i> Dashboard
                </a>
            </div>
        </div>

        <!-- Thông báo kết quả -->
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

        <!-- Khung tìm kiếm -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-body">
                <form method="get" action="${pageContext.request.contextPath}/customers" class="row g-2 align-items-center">
                    <input type="hidden" name="action" value="list" />
                    <div class="col-md-9 col-sm-8">
                        <div class="input-group">
                            <span class="input-group-text bg-white"><i class="bi bi-search text-muted"></i></span>
                            <input type="text" class="form-control" name="keyword" value="<c:out value='${keyword}' />"
                                   placeholder="Tìm kiếm theo họ tên, số điện thoại, email hoặc mã khách hàng..." />
                        </div>
                    </div>
                    <div class="col-md-3 col-sm-4 d-flex gap-2">
                        <button type="submit" class="btn btn-primary w-100">
                            <i class="bi bi-search me-1"></i> Tìm kiếm
                        </button>
                        <c:if test="${not empty keyword}">
                            <a href="${pageContext.request.contextPath}/customers" class="btn btn-outline-secondary" title="Xóa bộ lọc">
                                <i class="bi bi-x-lg"></i>
                            </a>
                        </c:if>
                    </div>
                </form>
            </div>
        </div>

        <!-- Bảng danh sách khách hàng -->
        <div class="card border-0 shadow-sm">
            <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                <span class="fw-semibold text-secondary">
                    Tổng số khách hàng: <strong class="text-primary">${customerList != null ? customerList.size() : 0}</strong>
                </span>
            </div>
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th scope="col" style="width: 100px;">Mã KH</th>
                            <th scope="col">Họ và tên</th>
                            <th scope="col">Số điện thoại</th>
                            <th scope="col">Email</th>
                            <th scope="col" class="text-center">Số lượng xe</th>
                            <th scope="col" class="text-center">Hạng hội viên</th>
                            <th scope="col" class="text-end" style="width: 180px;">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty customerList}">
                                <c:forEach var="c" items="${customerList}">
                                    <tr>
                                        <td>
                                            <span class="badge bg-secondary-subtle text-secondary font-monospace">
                                                <c:out value="${c.customerCode != null ? c.customerCode : ('CUST-' + c.customerId)}" />
                                            </span>
                                        </td>
                                        <td>
                                            <div class="fw-bold text-dark"><c:out value="${c.fullName}" /></div>
                                            <small class="text-muted"><c:out value="${c.address != null ? c.address : ''}" /></small>
                                        </td>
                                        <td>
                                            <i class="bi bi-telephone text-muted me-1"></i><c:out value="${c.phone}" />
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty c.email}">
                                                    <i class="bi bi-envelope text-muted me-1"></i><c:out value="${c.email}" />
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted fst-italic">Chưa có</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-center">
                                            <a href="${pageContext.request.contextPath}/vehicles?action=list&customerId=${c.customerId}"
                                               class="badge ${c.vehicleCount > 0 ? 'bg-primary-subtle text-primary border border-primary-subtle' : 'bg-light text-muted border'} text-decoration-none px-2 py-1">
                                                <i class="bi bi-car-front me-1"></i>${c.vehicleCount} xe
                                            </a>
                                        </td>
                                        <td class="text-center">
                                            <span class="badge ${c.membershipBadgeClass} px-2 py-1">
                                                <c:out value="${c.membershipLevel}" />
                                            </span>
                                        </td>
                                        <td class="text-end">
                                            <div class="btn-group btn-group-sm">
                                                <a href="${pageContext.request.contextPath}/customers?action=detail&id=${c.customerId}"
                                                   class="btn btn-outline-primary" title="Xem hồ sơ & DS Xe">
                                                    <i class="bi bi-eye"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/vehicles?action=add&customerId=${c.customerId}"
                                                   class="btn btn-outline-success" title="Thêm xe cho khách">
                                                    <i class="bi bi-plus-lg"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/customers?action=edit&id=${c.customerId}"
                                                   class="btn btn-outline-secondary" title="Sửa thông tin">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/customers?action=delete&id=${c.customerId}"
                                                   class="btn btn-outline-danger" title="Xóa khách hàng"
                                                   onclick="return confirm('Bạn có chắc chắn muốn xóa khách hàng [${c.fullName}]?');">
                                                    <i class="bi bi-trash"></i>
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="7" class="text-center py-5 text-muted">
                                        <i class="bi bi-person-x fs-1 d-block mb-2 text-secondary"></i>
                                        Không tìm thấy dữ liệu khách hàng phù hợp.
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
