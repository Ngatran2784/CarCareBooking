<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Bảng điều khiển - CarCareBooking" />
<%@ include file="../layout/header.jspf" %>
<%@ include file="../layout/sidebar.jspf" %>

<%-- dashboardRole do HomeServlet xác định: ADMIN | MANAGER | RECEPTIONIST | TECHNICIAN | CUSTOMER --%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="role" value="${empty dashboardRole ? 'CUSTOMER' : dashboardRole}" />
<c:set var="isShopWide" value="${role == 'ADMIN' || role == 'MANAGER' || role == 'RECEPTIONIST'}" />

<main class="content-wrapper">
    <div class="container-fluid">

        <c:if test="${dbConnected == false}">
            <div class="alert alert-warning d-flex align-items-center gap-2" role="alert">
                <i class="bi bi-database-exclamation fs-5"></i>
                <div class="small">
                    Chưa kết nối được cơ sở dữ liệu nên các số liệu đang hiển thị bằng 0.
                    Hãy kiểm tra MySQL (cổng 3307) và file <code>db.properties</code>.
                </div>
            </div>
        </c:if>

        <%-- ===== Banner chào mừng theo vai trò ===== --%>
        <div class="welcome-banner mb-4">
            <div class="d-flex flex-wrap justify-content-between align-items-center gap-3">
                <div>
                    <div class="small text-white-50 mb-1">
                        <i class="bi bi-person-badge me-1"></i>
                        <c:choose>
                            <c:when test="${role == 'ADMIN'}">Quản trị viên hệ thống</c:when>
                            <c:when test="${role == 'MANAGER'}">Quản lý cơ sở dịch vụ</c:when>
                            <c:when test="${role == 'RECEPTIONIST'}">Lễ tân tiếp nhận & điều phối</c:when>
                            <c:when test="${role == 'TECHNICIAN'}">Kỹ thuật viên chăm sóc xe</c:when>
                            <c:otherwise>Khách hàng</c:otherwise>
                        </c:choose>
                    </div>
                    <h3 class="fw-bold mb-1">Xin chào, <c:out value="${sessionScope.currentUser.fullName}" />!</h3>
                    <p class="mb-0 text-white-50">
                        <c:choose>
                            <c:when test="${role == 'ADMIN' || role == 'MANAGER'}">Theo dõi toàn bộ hoạt động của xưởng và quản lý hệ thống.</c:when>
                            <c:when test="${role == 'RECEPTIONIST'}">Tiếp nhận khách, check-in xe và điều phối công việc trong ngày.</c:when>
                            <c:when test="${role == 'TECHNICIAN'}">Xem công việc được giao và cập nhật tiến độ chăm sóc xe.</c:when>
                            <c:otherwise>Đặt lịch chăm sóc xe và theo dõi lịch sử dịch vụ của bạn.</c:otherwise>
                        </c:choose>
                    </p>
                </div>
                <c:if test="${role == 'CUSTOMER' || isShopWide}">
                    <a href="${ctx}/bookings" class="btn btn-light fw-semibold">
                        <i class="bi bi-calendar-plus me-1"></i> Đặt lịch hẹn
                    </a>
                </c:if>
                <c:if test="${role == 'TECHNICIAN'}">
                    <a href="${ctx}/jobs" class="btn btn-light fw-semibold">
                        <i class="bi bi-file-earmark-medical me-1"></i> Xem thẻ công việc
                    </a>
                </c:if>
            </div>
        </div>

        <%-- ===== Thẻ chỉ số (KPI) theo vai trò ===== --%>
        <div class="row g-3 mb-4">
            <c:choose>

                <%-- ADMIN / MANAGER / RECEPTIONIST: số liệu toàn xưởng --%>
                <c:when test="${isShopWide}">
                    <div class="col-sm-6 col-xl">
                        <div class="card kpi-card p-3 h-100">
                            <div class="d-flex align-items-center justify-content-between">
                                <div>
                                    <span class="text-muted small fw-semibold">Tổng số lịch hẹn</span>
                                    <h3 class="fw-bold mt-1 mb-0"><c:out value="${stats.totalBookings}" /></h3>
                                    <small class="text-muted">Đã ghi nhận</small>
                                </div>
                                <div class="kpi-icon bg-primary-subtle text-primary"><i class="bi bi-calendar-check fs-4"></i></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-6 col-xl">
                        <div class="card kpi-card p-3 h-100" style="border-left-color:#198754;">
                            <div class="d-flex align-items-center justify-content-between">
                                <div>
                                    <span class="text-muted small fw-semibold">Lịch hẹn hôm nay</span>
                                    <h3 class="fw-bold mt-1 mb-0 text-success"><c:out value="${stats.todayBookings}" /></h3>
                                    <small class="text-muted">Trong ngày</small>
                                </div>
                                <div class="kpi-icon bg-success-subtle text-success"><i class="bi bi-calendar-day fs-4"></i></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-6 col-xl">
                        <div class="card kpi-card p-3 h-100" style="border-left-color:#ffc107;">
                            <div class="d-flex align-items-center justify-content-between">
                                <div>
                                    <span class="text-muted small fw-semibold">Xe đang chăm sóc</span>
                                    <h3 class="fw-bold mt-1 mb-0 text-warning"><c:out value="${stats.inProgressVehicles}" /></h3>
                                    <small class="text-muted">Trong xưởng</small>
                                </div>
                                <div class="kpi-icon bg-warning-subtle text-warning"><i class="bi bi-car-front fs-4"></i></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-6 col-xl">
                        <div class="card kpi-card p-3 h-100" style="border-left-color:#0dcaf0;">
                            <div class="d-flex align-items-center justify-content-between">
                                <div>
                                    <span class="text-muted small fw-semibold">Công việc thực hiện</span>
                                    <h3 class="fw-bold mt-1 mb-0 text-info"><c:out value="${stats.activeJobs}" /></h3>
                                    <small class="text-muted">Đang thi công</small>
                                </div>
                                <div class="kpi-icon bg-info-subtle text-info"><i class="bi bi-tools fs-4"></i></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-12 col-xl">
                        <div class="card kpi-card p-3 h-100" style="border-left-color:#dc3545;">
                            <div class="d-flex align-items-center justify-content-between">
                                <div>
                                    <span class="text-muted small fw-semibold">Khoang đang dùng</span>
                                    <h3 class="fw-bold mt-1 mb-0 text-danger"><c:out value="${stats.occupiedBays}" /> / <c:out value="${stats.totalBays}" /></h3>
                                    <small class="text-muted">Công suất xưởng</small>
                                </div>
                                <div class="kpi-icon bg-danger-subtle text-danger"><i class="bi bi-bounding-box-circles fs-4"></i></div>
                            </div>
                        </div>
                    </div>
                </c:when>

                <%-- TECHNICIAN: chỉ công việc của chính mình --%>
                <c:when test="${role == 'TECHNICIAN'}">
                    <div class="col-md-4">
                        <div class="card kpi-card p-3 h-100">
                            <div class="d-flex align-items-center justify-content-between">
                                <div>
                                    <span class="text-muted small fw-semibold">Việc được giao</span>
                                    <h3 class="fw-bold mt-1 mb-0"><c:out value="${stats.myAssigned}" /></h3>
                                    <small class="text-muted">Chờ bắt đầu</small>
                                </div>
                                <div class="kpi-icon bg-primary-subtle text-primary"><i class="bi bi-clipboard-check fs-4"></i></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card kpi-card p-3 h-100" style="border-left-color:#ffc107;">
                            <div class="d-flex align-items-center justify-content-between">
                                <div>
                                    <span class="text-muted small fw-semibold">Đang thực hiện</span>
                                    <h3 class="fw-bold mt-1 mb-0 text-warning"><c:out value="${stats.myInProgress}" /></h3>
                                    <small class="text-muted">Đang thi công</small>
                                </div>
                                <div class="kpi-icon bg-warning-subtle text-warning"><i class="bi bi-tools fs-4"></i></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card kpi-card p-3 h-100" style="border-left-color:#198754;">
                            <div class="d-flex align-items-center justify-content-between">
                                <div>
                                    <span class="text-muted small fw-semibold">Hoàn thành hôm nay</span>
                                    <h3 class="fw-bold mt-1 mb-0 text-success"><c:out value="${stats.myCompletedToday}" /></h3>
                                    <small class="text-muted">Trong ngày</small>
                                </div>
                                <div class="kpi-icon bg-success-subtle text-success"><i class="bi bi-check2-circle fs-4"></i></div>
                            </div>
                        </div>
                    </div>
                </c:when>

                <%-- CUSTOMER: chỉ lịch hẹn của chính mình --%>
                <c:otherwise>
                    <div class="col-sm-6 col-xl-3">
                        <div class="card kpi-card p-3 h-100">
                            <div class="d-flex align-items-center justify-content-between">
                                <div>
                                    <span class="text-muted small fw-semibold">Tổng lịch hẹn của tôi</span>
                                    <h3 class="fw-bold mt-1 mb-0"><c:out value="${stats.myTotal}" /></h3>
                                    <small class="text-muted">Từ trước đến nay</small>
                                </div>
                                <div class="kpi-icon bg-primary-subtle text-primary"><i class="bi bi-calendar-check fs-4"></i></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-6 col-xl-3">
                        <div class="card kpi-card p-3 h-100" style="border-left-color:#0dcaf0;">
                            <div class="d-flex align-items-center justify-content-between">
                                <div>
                                    <span class="text-muted small fw-semibold">Sắp tới</span>
                                    <h3 class="fw-bold mt-1 mb-0 text-info"><c:out value="${stats.myUpcoming}" /></h3>
                                    <small class="text-muted">Chờ xác nhận / đã xác nhận</small>
                                </div>
                                <div class="kpi-icon bg-info-subtle text-info"><i class="bi bi-calendar-event fs-4"></i></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-6 col-xl-3">
                        <div class="card kpi-card p-3 h-100" style="border-left-color:#ffc107;">
                            <div class="d-flex align-items-center justify-content-between">
                                <div>
                                    <span class="text-muted small fw-semibold">Xe đang chăm sóc</span>
                                    <h3 class="fw-bold mt-1 mb-0 text-warning"><c:out value="${stats.myInProgress}" /></h3>
                                    <small class="text-muted">Đang ở xưởng</small>
                                </div>
                                <div class="kpi-icon bg-warning-subtle text-warning"><i class="bi bi-car-front fs-4"></i></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-6 col-xl-3">
                        <div class="card kpi-card p-3 h-100" style="border-left-color:#198754;">
                            <div class="d-flex align-items-center justify-content-between">
                                <div>
                                    <span class="text-muted small fw-semibold">Đã hoàn thành</span>
                                    <h3 class="fw-bold mt-1 mb-0 text-success"><c:out value="${stats.myCompleted}" /></h3>
                                    <small class="text-muted">Lượt dịch vụ</small>
                                </div>
                                <div class="kpi-icon bg-success-subtle text-success"><i class="bi bi-check2-circle fs-4"></i></div>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <%-- ===== Tình trạng khoang (chỉ vai trò quản lý / lễ tân) ===== --%>
        <c:if test="${isShopWide}">
            <div class="card mb-4">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <span><i class="bi bi-bounding-box me-2 text-primary"></i>Tình trạng khoang & cầu dịch vụ</span>
                    <c:if test="${role == 'ADMIN' || role == 'MANAGER'}">
                        <a href="${ctx}/bays" class="btn btn-sm btn-outline-primary">Chi tiết khoang</a>
                    </c:if>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty bays}">
                            <div class="row g-3">
                                <c:forEach var="bay" items="${bays}">
                                    <div class="col-md-6 col-xl-3">
                                        <div class="border rounded p-3 bg-white h-100">
                                            <div class="d-flex justify-content-between align-items-start mb-2">
                                                <span class="fw-bold"><c:out value="${bay.bayName}" /></span>
                                                <c:choose>
                                                    <c:when test="${bay.status == 'AVAILABLE'}"><span class="badge bg-success-subtle text-success border border-success-subtle">Trống</span></c:when>
                                                    <c:when test="${bay.status == 'OCCUPIED'}"><span class="badge bg-danger-subtle text-danger border border-danger-subtle">Đang phục vụ</span></c:when>
                                                    <c:otherwise><span class="badge bg-secondary-subtle text-secondary border">Bảo trì</span></c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="small text-muted"><i class="bi bi-tag me-1"></i>Loại: <c:out value="${bay.bayType}" /></div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-4 text-muted">
                                <i class="bi bi-inbox fs-1 d-block mb-2"></i>
                                Chưa có thông tin khoang dịch vụ trong hệ thống.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:if>

        <%-- ===== Lối tắt thao tác nhanh theo vai trò ===== --%>
        <div class="card">
            <div class="card-header"><i class="bi bi-lightning-charge me-2 text-primary"></i>Lối tắt thao tác nhanh</div>
            <div class="card-body">
                <div class="row g-3">
                    <c:choose>

                        <c:when test="${role == 'ADMIN' || role == 'MANAGER'}">
                            <div class="col-6 col-lg-3"><a class="quick-link" href="${ctx}/users"><i class="bi bi-people text-primary"></i><div><div class="fw-semibold small">Tài khoản</div><div class="text-muted small">Quản lý người dùng</div></div></a></div>
                            <div class="col-6 col-lg-3"><a class="quick-link" href="${ctx}/staff"><i class="bi bi-person-gear text-success"></i><div><div class="fw-semibold small">Nhân sự</div><div class="text-muted small">Kỹ thuật viên</div></div></a></div>
                            <div class="col-6 col-lg-3"><a class="quick-link" href="${ctx}/dispatch"><i class="bi bi-diagram-3 text-info"></i><div><div class="fw-semibold small">Điều phối xưởng</div><div class="text-muted small">Giao việc & khoang</div></div></a></div>
                            <div class="col-6 col-lg-3"><a class="quick-link" href="${ctx}/reports"><i class="bi bi-graph-up text-warning"></i><div><div class="fw-semibold small">Báo cáo</div><div class="text-muted small">Thống kê doanh thu</div></div></a></div>
                        </c:when>

                        <c:when test="${role == 'RECEPTIONIST'}">
                            <div class="col-6 col-lg-3"><a class="quick-link" href="${ctx}/bookings"><i class="bi bi-calendar2-plus text-primary"></i><div><div class="fw-semibold small">Đặt lịch</div><div class="text-muted small">Tiếp nhận lịch hẹn</div></div></a></div>
                            <div class="col-6 col-lg-3"><a class="quick-link" href="${ctx}/checkin"><i class="bi bi-box-arrow-in-down-right text-success"></i><div><div class="fw-semibold small">Check-in</div><div class="text-muted small">Tiếp nhận xe</div></div></a></div>
                            <div class="col-6 col-lg-3"><a class="quick-link" href="${ctx}/dispatch"><i class="bi bi-diagram-3 text-info"></i><div><div class="fw-semibold small">Điều phối</div><div class="text-muted small">Giao việc & khoang</div></div></a></div>
                            <div class="col-6 col-lg-3"><a class="quick-link" href="${ctx}/invoices"><i class="bi bi-receipt text-warning"></i><div><div class="fw-semibold small">Hóa đơn</div><div class="text-muted small">Thu ngân</div></div></a></div>
                        </c:when>

                        <c:when test="${role == 'TECHNICIAN'}">
                            <div class="col-6 col-lg-3"><a class="quick-link" href="${ctx}/jobs"><i class="bi bi-file-earmark-medical text-primary"></i><div><div class="fw-semibold small">Thẻ công việc</div><div class="text-muted small">Việc được giao</div></div></a></div>
                            <div class="col-6 col-lg-3"><a class="quick-link" href="${ctx}/inspections"><i class="bi bi-camera text-success"></i><div><div class="fw-semibold small">Biên bản & ảnh</div><div class="text-muted small">Hiện trạng xe</div></div></a></div>
                            <div class="col-6 col-lg-3"><a class="quick-link" href="${ctx}/additional-services"><i class="bi bi-plus-circle text-info"></i><div><div class="fw-semibold small">Dịch vụ phát sinh</div><div class="text-muted small">Đề xuất thêm</div></div></a></div>
                            <div class="col-6 col-lg-3"><a class="quick-link" href="${ctx}/quality-checks"><i class="bi bi-patch-check text-warning"></i><div><div class="fw-semibold small">Nghiệm thu QC</div><div class="text-muted small">Kiểm tra chất lượng</div></div></a></div>
                        </c:when>

                        <c:otherwise>
                            <div class="col-6 col-lg-4"><a class="quick-link" href="${ctx}/bookings"><i class="bi bi-calendar2-plus text-primary"></i><div><div class="fw-semibold small">Đặt lịch hẹn</div><div class="text-muted small">Chọn dịch vụ & giờ</div></div></a></div>
                            <div class="col-6 col-lg-4"><a class="quick-link" href="${ctx}/booking-history"><i class="bi bi-clock-history text-success"></i><div><div class="fw-semibold small">Lịch sử</div><div class="text-muted small">Các lần đã đặt</div></div></a></div>
                            <div class="col-12 col-lg-4"><a class="quick-link" href="${ctx}/reviews"><i class="bi bi-star text-warning"></i><div><div class="fw-semibold small">Đánh giá</div><div class="text-muted small">Gửi phản hồi dịch vụ</div></div></a></div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</main>

<%@ include file="../layout/footer.jspf" %>
