<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Bảng điều khiển trung tâm - CarCareBooking" />
<%@ include file="../layout/header.jspf" %>
<%@ include file="../layout/sidebar.jspf" %>

<main class="content-wrapper">
    <div class="container-fluid">
        <!-- Chào mừng & Tiêu đề trang -->
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
            <div>
                <h3 class="fw-bold mb-1">Bảng điều khiển vận hành</h3>
                <p class="text-secondary mb-0">
                    Hệ thống Quản lý Đặt lịch & Điều phối Dịch vụ Chăm sóc Xe chuyên nghiệp
                </p>
            </div>
            <div class="d-flex align-items-center gap-2">
                <span class="badge bg-light text-dark border px-3 py-2">
                    <i class="bi bi-person-badge text-primary me-1"></i>
                    Vai trò: <strong><c:out value="${sessionScope.currentUser.primaryRoleName}" /></strong>
                </span>
                <a href="${pageContext.request.contextPath}/bookings" class="btn btn-primary btn-sm px-3">
                    <i class="bi bi-calendar-plus me-1"></i> Tạo lịch hẹn
                </a>
            </div>
        </div>

        <!-- 5 Thẻ chỉ số KPI Vận hành Thực tế -->
        <div class="row g-3 mb-4">
            <!-- 1. Tổng số lịch hẹn -->
            <div class="col-sm-6 col-xl">
                <div class="card p-3 border-start border-4 border-primary h-100">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <span class="text-muted small fw-semibold">Tổng số lịch hẹn</span>
                            <h3 class="fw-bold mt-1 mb-0 text-dark"><c:out value="${stats.totalBookings}" /></h3>
                            <small class="text-muted">Đã ghi nhận</small>
                        </div>
                        <div class="bg-primary-subtle text-primary p-3 rounded-circle">
                            <i class="bi bi-calendar-check fs-4"></i>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 2. Lịch hẹn hôm nay -->
            <div class="col-sm-6 col-xl">
                <div class="card p-3 border-start border-4 border-success h-100">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <span class="text-muted small fw-semibold">Lịch hẹn hôm nay</span>
                            <h3 class="fw-bold mt-1 mb-0 text-success"><c:out value="${stats.todayBookings}" /></h3>
                            <small class="text-muted">Trong ngày</small>
                        </div>
                        <div class="bg-success-subtle text-success p-3 rounded-circle">
                            <i class="bi bi-calendar-day fs-4"></i>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 3. Xe đang chăm sóc -->
            <div class="col-sm-6 col-xl">
                <div class="card p-3 border-start border-4 border-warning h-100">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <span class="text-muted small fw-semibold">Xe đang chăm sóc</span>
                            <h3 class="fw-bold mt-1 mb-0 text-warning"><c:out value="${stats.inProgressVehicles}" /></h3>
                            <small class="text-muted">Trong xưởng</small>
                        </div>
                        <div class="bg-warning-subtle text-warning p-3 rounded-circle">
                            <i class="bi bi-car-front fs-4"></i>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 4. Công việc đang thực hiện -->
            <div class="col-sm-6 col-xl">
                <div class="card p-3 border-start border-4 border-info h-100">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <span class="text-muted small fw-semibold">Công việc thực hiện</span>
                            <h3 class="fw-bold mt-1 mb-0 text-info"><c:out value="${stats.activeJobs}" /></h3>
                            <small class="text-muted">Đang thi công</small>
                        </div>
                        <div class="bg-info-subtle text-info p-3 rounded-circle">
                            <i class="bi bi-tools fs-4"></i>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 5. Khoang dịch vụ đang sử dụng -->
            <div class="col-sm-12 col-xl">
                <div class="card p-3 border-start border-4 border-danger h-100">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <span class="text-muted small fw-semibold">Khoang đang dùng</span>
                            <h3 class="fw-bold mt-1 mb-0 text-danger">
                                <c:out value="${stats.occupiedBays}" /> / <c:out value="${stats.totalBays}" />
                            </h3>
                            <small class="text-muted">Công suất xưởng</small>
                        </div>
                        <div class="bg-danger-subtle text-danger p-3 rounded-circle">
                            <i class="bi bi-bounding-box-circles fs-4"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row g-4 mb-4">
            <!-- Sơ đồ trạng thái Khoang / Cầu Dịch vụ -->
            <div class="col-lg-8">
                <div class="card h-100">
                    <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
                        <span class="fw-bold"><i class="bi bi-bounding-box me-2 text-primary"></i>Tình trạng Khoang & Cầu dịch vụ trực tiếp</span>
                        <a href="${pageContext.request.contextPath}/bays" class="btn btn-sm btn-outline-primary">Chi tiết khoang</a>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty bays}">
                                <div class="row g-3">
                                    <c:forEach var="bay" items="${bays}">
                                        <div class="col-md-6 col-xl-4">
                                            <div class="border rounded p-3 bg-white h-100">
                                                <div class="d-flex justify-content-between align-items-start mb-2">
                                                    <span class="fw-bold text-dark"><c:out value="${bay.bayName}" /></span>
                                                    <c:choose>
                                                        <c:when test="${bay.status == 'AVAILABLE'}">
                                                            <span class="badge bg-success-subtle text-success border border-success-subtle">Trống</span>
                                                        </c:when>
                                                        <c:when test="${bay.status == 'OCCUPIED'}">
                                                            <span class="badge bg-danger-subtle text-danger border border-danger-subtle">Đang phục vụ</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-secondary-subtle text-secondary border">Bảo trì</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div class="small text-muted mb-2">
                                                    <i class="bi bi-tag me-1"></i>Loại: <c:out value="${bay.bayType}" />
                                                </div>
                                                <div class="progress" style="height: 6px;">
                                                    <div class="progress-bar ${bay.status == 'OCCUPIED' ? 'bg-danger w-100' : 'bg-success w-0'}"></div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-5 text-muted">
                                    <i class="bi bi-inbox fs-1 mb-2 d-block"></i>
                                    <p class="mb-0">Chưa có thông tin khoang dịch vụ trong hệ thống.</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Bảng Thông báo Nghiệp vụ & Hướng dẫn Vận hành -->
            <div class="col-lg-4">
                <div class="card h-100">
                    <div class="card-header bg-white py-3">
                        <span class="fw-bold"><i class="bi bi-bell me-2 text-warning"></i>Thông báo nghiệp vụ xưởng</span>
                    </div>
                    <div class="card-body p-0">
                        <ul class="list-group list-group-flush small">
                            <li class="list-group-item p-3">
                                <div class="d-flex align-items-start gap-2">
                                    <i class="bi bi-info-circle-fill text-primary mt-1"></i>
                                    <div>
                                        <div class="fw-semibold">Quy trình tiếp nhận xe (Check-in)</div>
                                        <div class="text-muted">Kiểm tra ngoại thất và chụp ảnh hiện trạng xe trước khi thi công dịch vụ.</div>
                                    </div>
                                </div>
                            </li>
                            <li class="list-group-item p-3">
                                <div class="d-flex align-items-start gap-2">
                                    <i class="bi bi-check-circle-fill text-success mt-1"></i>
                                    <div>
                                        <div class="fw-semibold">Nghiệm thu chất lượng (QC)</div>
                                        <div class="text-muted">Kỹ thuật viên hoàn tất thi công cần gửi nghiệm thu QC trước khi lập hóa đơn giao xe.</div>
                                    </div>
                                </div>
                            </li>
                            <li class="list-group-item p-3">
                                <div class="d-flex align-items-start gap-2">
                                    <i class="bi bi-shield-lock-fill text-info mt-1"></i>
                                    <div>
                                        <div class="fw-semibold">Bảo mật tài khoản</div>
                                        <div class="text-muted">Hệ thống áp dụng mã hóa mật khẩu chuẩn BCrypt và phân quyền vai trò theo từng vị trí.</div>
                                    </div>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <!-- Tác vụ thao tác nhanh (Quick Operations) -->
        <div class="card">
            <div class="card-header bg-white py-3">
                <span class="fw-bold"><i class="bi bi-lightning-charge me-2 text-primary"></i>Lối tắt thao tác nhanh</span>
            </div>
            <div class="card-body">
                <div class="row g-2">
                    <div class="col-6 col-md-3">
                        <a href="${pageContext.request.contextPath}/bookings" class="btn btn-outline-secondary w-100 py-3 text-start d-flex align-items-center gap-2">
                            <i class="bi bi-calendar2-plus fs-3 text-primary"></i>
                            <div>
                                <div class="fw-semibold small">Đặt lịch chăm sóc</div>
                                <div class="text-muted" style="font-size: 0.75rem;">Tiếp nhận lịch hẹn</div>
                            </div>
                        </a>
                    </div>
                    <div class="col-6 col-md-3">
                        <a href="${pageContext.request.contextPath}/checkin" class="btn btn-outline-secondary w-100 py-3 text-start d-flex align-items-center gap-2">
                            <i class="bi bi-box-arrow-in-down-right fs-3 text-success"></i>
                            <div>
                                <div class="fw-semibold small">Tiếp nhận xe</div>
                                <div class="text-muted" style="font-size: 0.75rem;">Check-in & Ảnh xe</div>
                            </div>
                        </a>
                    </div>
                    <div class="col-6 col-md-3">
                        <a href="${pageContext.request.contextPath}/dispatch" class="btn btn-outline-secondary w-100 py-3 text-start d-flex align-items-center gap-2">
                            <i class="bi bi-diagram-3 fs-3 text-info"></i>
                            <div>
                                <div class="fw-semibold small">Điều phối xưởng</div>
                                <div class="text-muted" style="font-size: 0.75rem;">Giao việc & Khoang</div>
                            </div>
                        </a>
                    </div>
                    <div class="col-6 col-md-3">
                        <a href="${pageContext.request.contextPath}/invoices" class="btn btn-outline-secondary w-100 py-3 text-start d-flex align-items-center gap-2">
                            <i class="bi bi-receipt fs-3 text-warning"></i>
                            <div>
                                <div class="fw-semibold small">Hóa đơn thanh toán</div>
                                <div class="text-muted" style="font-size: 0.75rem;">Thu ngân & Xuất hóa đơn</div>
                            </div>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<%@ include file="../layout/footer.jspf" %>
