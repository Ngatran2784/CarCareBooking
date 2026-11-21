<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${pageTitle != null ? pageTitle : 'Hồ sơ khách hàng - CarCareBooking'}" />
<%@ include file="../layout/header.jspf" %>
<%@ include file="../layout/sidebar.jspf" %>

<main class="content-wrapper">
    <div class="container-fluid">
        <!-- Tiêu đề trang -->
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
            <div>
                <h3 class="fw-bold mb-1">
                    <c:choose>
                        <c:when test="${isSelfProfile}">
                            <i class="bi bi-person-vcard text-primary me-2"></i>Hồ sơ cá nhân của tôi
                        </c:when>
                        <c:when test="${isNew}">
                            <i class="bi bi-person-plus text-primary me-2"></i>Thêm khách hàng mới
                        </c:when>
                        <c:otherwise>
                            <i class="bi bi-person-badge text-primary me-2"></i>Hồ sơ khách hàng: <c:out value="${customer.fullName}" />
                        </c:otherwise>
                    </c:choose>
                </h3>
                <p class="text-muted small mb-0">
                    <c:choose>
                        <c:when test="${isSelfProfile}">
                            Quản lý thông tin liên hệ và phương tiện chăm sóc cá nhân của bạn tại trung tâm.
                        </c:when>
                        <c:otherwise>
                            Cập nhật thông tin chi tiết và theo dõi phương tiện gắn liền với hồ sơ khách hàng.
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
            <div>
                <c:choose>
                    <c:when test="${isSelfProfile}">
                        <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-secondary btn-sm">
                            <i class="bi bi-arrow-left"></i> Quay lại Dashboard
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/customers?action=list" class="btn btn-outline-secondary btn-sm">
                            <i class="bi bi-arrow-left"></i> Danh sách khách hàng
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Thông báo phản hồi -->
        <c:if test="${not empty param.msg}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i><c:out value="${param.msg}" />
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i><c:out value="${errorMessage}" />
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="row g-4">
            <!-- Cột thông tin hồ sơ -->
            <div class="${isNew ? 'col-lg-8 mx-auto' : 'col-lg-5 col-xl-4'}">
                <div class="card border-0 shadow-sm h-100">
                    <div class="card-header bg-white py-3 border-bottom">
                        <h5 class="card-title mb-0 fw-semibold">
                            <i class="bi bi-card-text text-primary me-2"></i>Thông tin tài khoản & Hồ sơ
                        </h5>
                    </div>
                    <div class="card-body">
                        <form method="post" action="${pageContext.request.contextPath}/customers">
                            <c:choose>
                                <c:when test="${isSelfProfile}">
                                    <input type="hidden" name="action" value="updateProfile" />
                                </c:when>
                                <c:when test="${isNew}">
                                    <input type="hidden" name="action" value="save" />
                                </c:when>
                                <c:otherwise>
                                    <input type="hidden" name="action" value="update" />
                                    <input type="hidden" name="customerId" value="${customer.customerId}" />
                                </c:otherwise>
                            </c:choose>

                            <!-- Mã KH & Hạng thẻ -->
                            <c:if test="${!isNew}">
                                <div class="row mb-3 g-2">
                                    <div class="col-6">
                                        <label class="form-label text-muted small mb-1">Mã khách hàng</label>
                                        <div class="fw-bold font-monospace text-dark py-1">
                                            <c:out value="${customer.customerCode != null ? customer.customerCode : ('CUST-' + customer.customerId)}" />
                                        </div>
                                    </div>
                                    <div class="col-6">
                                        <label class="form-label text-muted small mb-1">Hạng thành viên</label>
                                        <div>
                                            <span class="badge ${customer.membershipBadgeClass} px-2 py-1">
                                                <i class="bi bi-shield-check me-1"></i><c:out value="${customer.membershipLevel}" />
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                <hr class="my-3 text-muted">
                            </c:if>

                            <!-- Họ và tên -->
                            <div class="mb-3">
                                <label for="fullName" class="form-label fw-semibold">Họ và tên <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="bi bi-person"></i></span>
                                    <input type="text" class="form-control" id="fullName" name="fullName"
                                           value="<c:out value='${customer.fullName}' />" required minlength="2" maxlength="100" />
                                </div>
                            </div>

                            <!-- Số điện thoại -->
                            <div class="mb-3">
                                <label for="phone" class="form-label fw-semibold">Số điện thoại <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="bi bi-telephone"></i></span>
                                    <input type="tel" class="form-control font-monospace" id="phone" name="phone"
                                           value="<c:out value='${customer.phone}' />" required pattern="0[0-9]{9}"
                                           placeholder="0912345678" />
                                </div>
                                <div class="form-text small">Số điện thoại gồm 10 chữ số bắt đầu bằng số 0.</div>
                            </div>

                            <!-- Email -->
                            <div class="mb-3">
                                <label for="email" class="form-label fw-semibold">Địa chỉ Email</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="bi bi-envelope"></i></span>
                                    <input type="email" class="form-control" id="email" name="email"
                                           value="<c:out value='${customer.email}' />" placeholder="example@domain.com" />
                                </div>
                            </div>

                            <!-- Địa chỉ -->
                            <div class="mb-3">
                                <label for="address" class="form-label fw-semibold">Địa chỉ liên hệ</label>
                                <textarea class="form-control" id="address" name="address" rows="3"
                                          placeholder="Số nhà, tên đường, phường/xã, quận/huyện..."><c:out value='${customer.address}' /></textarea>
                            </div>

                            <!-- Hạng thành viên nếu là Staff quản trị chỉnh sửa -->
                            <c:if test="${!isSelfProfile}">
                                <div class="mb-4">
                                    <label for="membershipLevel" class="form-label fw-semibold">Phân hạng khách hàng</label>
                                    <select class="form-select" id="membershipLevel" name="membershipLevel">
                                        <option value="STANDARD" ${'STANDARD' == customer.membershipLevel ? 'selected' : ''}>STANDARD (Tiêu chuẩn)</option>
                                        <option value="SILVER" ${'SILVER' == customer.membershipLevel ? 'selected' : ''}>SILVER (Bạc - Ưu đãi 5%)</option>
                                        <option value="GOLD" ${'GOLD' == customer.membershipLevel ? 'selected' : ''}>GOLD (Vàng - Ưu đãi 10%)</option>
                                        <option value="PLATINUM" ${'PLATINUM' == customer.membershipLevel ? 'selected' : ''}>PLATINUM (Bạch Kim - Ưu đãi 15%)</option>
                                    </select>
                                </div>
                            </c:if>

                            <!-- Nút hành động -->
                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary py-2 fw-semibold">
                                    <i class="bi bi-save me-1"></i>
                                    <c:choose>
                                        <c:when test="${isSelfProfile}">Lưu cập nhật hồ sơ</c:when>
                                        <c:when test="${isNew}">Tạo khách hàng mới</c:when>
                                        <c:otherwise>Cập nhật thông tin</c:otherwise>
                                    </c:choose>
                                </button>
                                <c:if test="${!isNew && !isSelfProfile}">
                                    <a href="${pageContext.request.contextPath}/customers?action=list" class="btn btn-outline-secondary">Hủy bỏ</a>
                                </c:if>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Cột danh sách phương tiện sở hữu -->
            <c:if test="${!isNew}">
                <div class="${isSelfProfile ? 'col-lg-7 col-xl-8' : 'col-lg-7 col-xl-8'}">
                    <div class="card border-0 shadow-sm h-100">
                        <div class="card-header bg-white py-3 d-flex flex-wrap justify-content-between align-items-center gap-2 border-bottom">
                            <div>
                                <h5 class="card-title mb-0 fw-semibold">
                                    <i class="bi bi-truck-front text-primary me-2"></i>Phương tiện xe sở hữu
                                </h5>
                                <small class="text-muted">Các xe đã đăng ký phục vụ đặt lịch và điều phối dịch vụ.</small>
                            </div>
                            <div>
                                <c:choose>
                                    <c:when test="${isSelfProfile}">
                                        <a href="${pageContext.request.contextPath}/vehicles?action=add" class="btn btn-sm btn-primary">
                                            <i class="bi bi-plus-circle me-1"></i> Thêm xe của tôi
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/vehicles?action=add&customerId=${customer.customerId}" class="btn btn-sm btn-primary">
                                            <i class="bi bi-plus-circle me-1"></i> Thêm xe cho khách
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th scope="col" style="width: 130px;">Biển số</th>
                                            <th scope="col">Hãng & Dòng xe</th>
                                            <th scope="col" class="text-center">Phân loại</th>
                                            <th scope="col" class="text-center">Năm SX</th>
                                            <th scope="col" class="text-center">Màu sắc</th>
                                            <th scope="col" class="text-end" style="width: 140px;">Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:choose>
                                            <c:when test="${not empty vehicles}">
                                                <c:forEach var="v" items="${vehicles}">
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
                                                    <td colspan="6" class="text-center py-5 text-muted">
                                                        <i class="bi bi-car-front fs-1 d-block mb-2 text-secondary"></i>
                                                        Chưa có phương tiện xe nào được đăng ký.
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
            </c:if>
        </div>
    </div>
</main>

<%@ include file="../layout/footer.jspf" %>
