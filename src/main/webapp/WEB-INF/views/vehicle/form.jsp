<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${pageTitle != null ? pageTitle : 'Thông tin phương tiện - CarCareBooking'}" />
<%@ include file="../layout/header.jspf" %>
<%@ include file="../layout/sidebar.jspf" %>

<main class="content-wrapper">
    <div class="container-fluid">
        <!-- Tiêu đề trang -->
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
            <div>
                <h3 class="fw-bold mb-1">
                    <c:choose>
                        <c:when test="${isNew}">
                            <i class="bi bi-plus-circle text-primary me-2"></i>Thêm phương tiện mới
                        </c:when>
                        <c:otherwise>
                            <i class="bi bi-pencil-square text-primary me-2"></i>Chỉnh sửa phương tiện: <span class="font-monospace"><c:out value="${vehicle.licensePlate}" /></span>
                        </c:otherwise>
                    </c:choose>
                </h3>
                <p class="text-muted small mb-0">Cung cấp đầy đủ thông tin phương tiện phục vụ quy trình tiếp nhận, điều phối và phân khoang.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/vehicles?action=list" class="btn btn-outline-secondary btn-sm">
                    <i class="bi bi-arrow-left"></i> Quay lại danh sách xe
                </a>
            </div>
        </div>

        <!-- Thông báo lỗi nếu có -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i><c:out value="${errorMessage}" />
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="row">
            <div class="col-lg-8 col-xl-7 mx-auto">
                <div class="card border-0 shadow-sm">
                    <div class="card-header bg-white py-3 border-bottom">
                        <h5 class="card-title mb-0 fw-semibold text-secondary">
                            <i class="bi bi-car-front text-primary me-2"></i>Thông số kỹ thuật phương tiện
                        </h5>
                    </div>
                    <div class="card-body p-4">
                        <form method="post" action="${pageContext.request.contextPath}/vehicles">
                            <input type="hidden" name="action" value="${isNew ? 'save' : 'update'}" />
                            <c:if test="${!isNew}">
                                <input type="hidden" name="vehicleId" value="${vehicle.vehicleId}" />
                            </c:if>

                            <!-- Chọn chủ sở hữu nếu là Nhân viên -->
                            <c:choose>
                                <c:when test="${!isCustomerView}">
                                    <div class="mb-3">
                                        <label for="customerId" class="form-label fw-semibold">Khách hàng sở hữu <span class="text-danger">*</span></label>
                                        <select class="form-select" id="customerId" name="customerId" required>
                                            <option value="">-- Chọn khách hàng --</option>
                                            <c:forEach var="cust" items="${customerList}">
                                                <option value="${cust.customerId}"
                                                    ${(vehicle.customerId == cust.customerId || param.customerId == cust.customerId) ? 'selected' : ''}>
                                                    <c:out value="${cust.fullName}" /> - <c:out value="${cust.phone}" /> (<c:out value="${cust.customerCode != null ? cust.customerCode : ('CUST-' + cust.customerId)}" />)
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <div class="form-text small">Phương tiện phải được gắn liền với một khách hàng trong hệ thống.</div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="mb-3 p-3 bg-light rounded border">
                                        <div class="small text-muted mb-1">Chủ sở hữu phương tiện</div>
                                        <div class="fw-bold text-dark">
                                            <i class="bi bi-person-circle me-1"></i><c:out value="${customer.fullName}" />
                                            <span class="text-muted ms-2">(<c:out value="${customer.phone}" />)</span>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>

                            <div class="row g-3">
                                <!-- Biển số xe -->
                                <div class="col-md-6">
                                    <label for="licensePlate" class="form-label fw-semibold">Biển số xe <span class="text-danger">*</span></label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light"><i class="bi bi-card-heading"></i></span>
                                        <input type="text" class="form-control font-monospace text-uppercase" id="licensePlate" name="licensePlate"
                                               value="<c:out value='${vehicle.licensePlate}' />" required maxlength="20"
                                               placeholder="VD: 30A-123.45" />
                                    </div>
                                    <div class="form-text small">Viết hoa, có thể có dấu gạch ngang hoặc chấm.</div>
                                </div>

                                <!-- Phân loại xe -->
                                <div class="col-md-6">
                                    <label for="vehicleType" class="form-label fw-semibold">Phân loại xe (Form xe) <span class="text-danger">*</span></label>
                                    <select class="form-select" id="vehicleType" name="vehicleType" required>
                                        <c:forEach var="type" items="${vehicleTypes}">
                                            <option value="${type}" ${type == vehicle.vehicleType ? 'selected' : ''}>
                                                <c:out value="${type}" />
                                                <c:choose>
                                                    <c:when test="${type == 'SEDAN'}"> (Sedan 4-5 chỗ)</c:when>
                                                    <c:when test="${type == 'SUV'}"> (SUV 7 chỗ gầm cao)</c:when>
                                                    <c:when test="${type == 'CUV'}"> (Crossover 5 chỗ gầm cao)</c:when>
                                                    <c:when test="${type == 'MPV'}"> (Xe đa dụng MPV 7 chỗ)</c:when>
                                                    <c:when test="${type == 'HATCHBACK'}"> (Hatchback đô thị cỡ nhỏ)</c:when>
                                                    <c:when test="${type == 'PICKUP'}"> (Bán tải / Xe tải nhỏ)</c:when>
                                                    <c:otherwise> (Loại khác)</c:otherwise>
                                                </c:choose>
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <div class="form-text small">Phân loại dùng để xác định kích cỡ khoang & thời lượng dịch vụ.</div>
                                </div>

                                <!-- Hãng xe -->
                                <div class="col-md-6">
                                    <label for="brand" class="form-label fw-semibold">Hãng sản xuất <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="brand" name="brand"
                                           value="<c:out value='${vehicle.brand}' />" required maxlength="50"
                                           placeholder="VD: Toyota, Mazda, Honda, Hyundai, VinFast..." />
                                </div>

                                <!-- Dòng xe (Model) -->
                                <div class="col-md-6">
                                    <label for="model" class="form-label fw-semibold">Dòng xe (Model) <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="model" name="model"
                                           value="<c:out value='${vehicle.model}' />" required maxlength="50"
                                           placeholder="VD: Camry, CX-5, CR-V, Accent, VF8..." />
                                </div>

                                <!-- Năm sản xuất -->
                                <div class="col-md-6">
                                    <label for="year" class="form-label fw-semibold">Năm sản xuất</label>
                                    <input type="number" class="form-control" id="year" name="year"
                                           value="${vehicle.year != null && vehicle.year > 0 ? vehicle.year : ''}"
                                           min="1980" max="2030" step="1" placeholder="VD: 2022" />
                                </div>

                                <!-- Màu sơn -->
                                <div class="col-md-6">
                                    <label for="color" class="form-label fw-semibold">Màu sơn xe</label>
                                    <input type="text" class="form-control" id="color" name="color"
                                           value="<c:out value='${vehicle.color}' />" maxlength="30"
                                           placeholder="VD: Trắng ngọc trai, Đen, Xám xi măng..." />
                                </div>
                            </div>

                            <hr class="my-4">

                            <!-- Nút submit & hủy -->
                            <div class="d-flex justify-content-end gap-2">
                                <a href="${pageContext.request.contextPath}/vehicles?action=list" class="btn btn-outline-secondary px-4">
                                    Hủy bỏ
                                </a>
                                <button type="submit" class="btn btn-primary px-4 fw-semibold">
                                    <i class="bi bi-check2-circle me-1"></i>
                                    <c:choose>
                                        <c:when test="${isNew}">Thêm phương tiện</c:when>
                                        <c:otherwise>Lưu thay đổi</c:otherwise>
                                    </c:choose>
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<%@ include file="../layout/footer.jspf" %>
