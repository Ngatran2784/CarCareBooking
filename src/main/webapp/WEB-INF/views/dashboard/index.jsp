<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Bảng điều khiển - CarCareBooking" />

<%@ include file="../layout/header.jspf" %>

<%@ include file="../layout/sidebar.jspf" %>

<main class="content-wrapper">

    <div class="container-fluid">

        <!-- Page Header -->

        <div class="d-flex justify-content-between align-items-center mb-4">

            <div>

                <h2 class="fw-bold mb-1">Bảng điều khiển tổng quan</h2>

                <p class="text-muted mb-0">Hệ thống quản lý đặt lịch và điều phối dịch vụ chăm sóc xe hơi (Detailing &amp; Maintenance)</p>

            </div>

            <div>

                <span class="badge bg-primary px-3 py-2 fs-6">Tomcat 10.1 / Jakarta Servlet 6.0</span>

            </div>

        </div>

        <!-- System Status Banner -->

        <div class="row g-3 mb-4">

            <div class="col-md-4">

                <div class="card p-3 border-start border-4 border-success">

                    <div class="d-flex align-items-center justify-content-between">

                        <div>

                            <span class="text-muted small">Trạng thái Web Server</span>

                            <h5 class="fw-bold mt-1 mb-0 text-success">Đang hoạt động</h5>

                            <small class="text-secondary">Apache Tomcat 10.1 / Java 17</small>

                        </div>

                        <i class="bi bi-server text-success fs-2"></i>

                    </div>

                </div>

            </div>

            <div class="col-md-4">

                <div class="card p-3 border-start border-4 ${dbConnected ? 'border-primary' : 'border-warning'}">

                    <div class="d-flex align-items-center justify-content-between">

                        <div>

                            <span class="text-muted small">Cơ sở dữ liệu MySQL 8.x</span>

                                <c:choose>

                                    <c:when test="${dbConnected}">

                                        <h5 class="fw-bold mt-1 mb-0 text-primary">Đã kết nối</h5>

                                        <small class="text-muted">carcare_db sẵn sàng</small>

                                    </c:when>

                                    <c:otherwise>

                                        <h5 class="fw-bold mt-1 mb-0 text-warning">Chưa kết nối CSDL</h5>

                                        <small class="text-muted">Chạy chế độ thử nghiệm độc lập</small>

                                    </c:otherwise>

                                </c:choose>

                        </div>

                        <i class="bi bi-database ${dbConnected ? 'text-primary' : 'text-warning'} fs-2"></i>

                    </div>

                </div>

            </div>

            <div class="col-md-4">

                <div class="card p-3 border-start border-4 border-info">

                    <div class="d-flex align-items-center justify-content-between">

                        <div>

                            <span class="text-muted small">Giai đoạn phát triển</span>

                            <h5 class="fw-bold mt-1 mb-0 text-info">Sprint 1 - Hoàn tất khung</h5>

                            <small class="text-muted">Mã nguồn sẵn sàng cho Sprint 2-7</small>

                        </div>

                        <i class="bi bi-check2-circle text-info fs-2"></i>

                    </div>

                </div>

            </div>

        </div>

        <!-- Sprint Roadmap Overview -->

        <div class="card mb-4">

            <div class="card-header d-flex justify-content-between align-items-center">

                <span><i class="bi bi-kanban me-2"></i>Tiến độ lộ trình 7 Sprint phát triển</span>

                <span class="badge bg-secondary">Chuẩn bị bàn giao Sprint 1</span>

            </div>

            <div class="card-body">

                <div class="row g-3">

                    <div class="col-md-6 col-lg-3">

                        <div class="border rounded p-3 bg-white h-100">

                            <div class="d-flex justify-content-between mb-2">

                                <span class="badge bg-success">Sprint 1</span>

                                <span class="badge bg-light text-dark">Khung sườn</span>

                            </div>

                            <h6 class="fw-bold mb-1">Kiến trúc MVC &amp; Xác thực</h6>

                            <p class="small text-muted mb-2">pom.xml, web.xml, DBContext, Filters, Auth, Layout JSPF, Dashboard.</p>

                            <a href="${pageContext.request.contextPath}/users" class="btn btn-sm btn-outline-success">Mở module</a>

                        </div>

                    </div>

                    <div class="col-md-6 col-lg-3">

                        <div class="border rounded p-3 bg-white h-100">

                            <div class="d-flex justify-content-between mb-2">

                                <span class="badge bg-info text-dark">Sprint 2</span>

                                <span class="badge bg-light text-muted">Kế hoạch</span>

                            </div>

                            <h6 class="fw-bold mb-1">Khách hàng, Xe &amp; Dịch vụ</h6>

                            <p class="small text-muted mb-2">Quản lý khách hàng, phương tiện tiếp nhận, bảng giá dịch vụ.</p>

                            <a href="${pageContext.request.contextPath}/services" class="btn btn-sm btn-outline-info">Mở module</a>

                        </div>

                    </div>

                    <div class="col-md-6 col-lg-3">

                        <div class="border rounded p-3 bg-white h-100">

                            <div class="d-flex justify-content-between mb-2">

                                <span class="badge bg-info text-dark">Sprint 3</span>

                                <span class="badge bg-light text-muted">Kế hoạch</span>

                            </div>

                            <h6 class="fw-bold mb-1">Nhân sự, Ca &amp; Khoang</h6>

                            <p class="small text-muted mb-2">Kỹ thuật viên, kỹ năng chuyên môn, lịch ca làm việc và khoang cầu.</p>

                            <a href="${pageContext.request.contextPath}/bays" class="btn btn-sm btn-outline-info">Mở module</a>

                        </div>

                    </div>

                    <div class="col-md-6 col-lg-3">

                        <div class="border rounded p-3 bg-white h-100">

                            <div class="d-flex justify-content-between mb-2">

                                <span class="badge bg-info text-dark">Sprint 4</span>

                                <span class="badge bg-light text-muted">Kế hoạch</span>

                            </div>

                            <h6 class="fw-bold mb-1">Đặt lịch &amp; Check-in</h6>

                            <p class="small text-muted mb-2">Đặt hẹn trực tuyến, chọn dịch vụ, tiếp nhận xe vào xưởng.</p>

                            <a href="${pageContext.request.contextPath}/bookings" class="btn btn-sm btn-outline-info">Mở module</a>

                        </div>

                    </div>

                    <div class="col-md-6 col-lg-3">

                        <div class="border rounded p-3 bg-white h-100">

                            <div class="d-flex justify-content-between mb-2">

                                <span class="badge bg-info text-dark">Sprint 5</span>

                                <span class="badge bg-light text-muted">Kế hoạch</span>

                            </div>

                            <h6 class="fw-bold mb-1">Điều phối &amp; Thẻ Job Card</h6>

                            <p class="small text-muted mb-2">Giao thợ vào khoang, chụp ảnh kiểm tra xe ban đầu, theo dõi tiến độ.</p>

                            <a href="${pageContext.request.contextPath}/dispatch" class="btn btn-sm btn-outline-info">Mở module</a>

                        </div>

                    </div>

                    <div class="col-md-6 col-lg-3">

                        <div class="border rounded p-3 bg-white h-100">

                            <div class="d-flex justify-content-between mb-2">

                                <span class="badge bg-info text-dark">Sprint 6</span>

                                <span class="badge bg-light text-muted">Kế hoạch</span>

                            </div>

                            <h6 class="fw-bold mb-1">Phát sinh &amp; Nghiệm thu QC</h6>

                            <p class="small text-muted mb-2">Yêu cầu phát sinh cần khách duyệt, quy trình kiểm định chất lượng.</p>

                            <a href="${pageContext.request.contextPath}/quality-checks" class="btn btn-sm btn-outline-info">Mở module</a>

                        </div>

                    </div>

                    <div class="col-md-6 col-lg-3">

                        <div class="border rounded p-3 bg-white h-100">

                            <div class="d-flex justify-content-between mb-2">

                                <span class="badge bg-info text-dark">Sprint 7</span>

                                <span class="badge bg-light text-muted">Kế hoạch</span>

                            </div>

                            <h6 class="fw-bold mb-1">Hóa đơn, Thanh toán &amp; Báo cáo</h6>

                            <p class="small text-muted mb-2">Xuất hóa đơn, tính VAT, thu tiền, đánh giá sao và biểu đồ doanh thu.</p>

                            <a href="${pageContext.request.contextPath}/reports" class="btn btn-sm btn-outline-info">Mở module</a>

                        </div>

                    </div>

                </div>

            </div>

        </div>

        <!-- Quick Access Table -->

        <div class="card">

            <div class="card-header">

                <i class="bi bi-link-45deg me-2"></i>Danh mục Servlet và Liên kết kiểm thử trực tiếp

            </div>

            <div class="card-body p-0">

                <div class="table-responsive">

                    <table class="table table-hover align-middle mb-0">

                        <thead>

                            <tr>

                                <th>Servlet Controller</th>

                                <th>URL Mapping</th>

                                <th>Sprint</th>

                                <th>Giao diện JSP tương ứng</th>

                                <th>Trạng thái</th>

                            </tr>

                        </thead>

                        <tbody>

                            <tr>

                                <td><strong>HomeServlet</strong></td>

                                <td><code>/home</code></td>

                                <td><span class="badge bg-success">Sprint 1</span></td>

                                <td><code>WEB-INF/views/dashboard/index.jsp</code></td>

                                <td><span class="badge bg-success">Hoạt động</span></td>

                            </tr>

                            <tr>

                                <td><strong>AuthServlet</strong></td>

                                <td><code>/auth</code></td>

                                <td><span class="badge bg-success">Sprint 1</span></td>

                                <td><code>WEB-INF/views/auth/login.jsp</code></td>

                                <td><a href="${pageContext.request.contextPath}/auth" class="btn btn-sm btn-outline-secondary">Truy cập</a></td>

                            </tr>

                            <tr>

                                <td><strong>UserServlet</strong></td>

                                <td><code>/users</code></td>

                                <td><span class="badge bg-success">Sprint 1</span></td>

                                <td><code>WEB-INF/views/user/list.jsp</code></td>

                                <td><a href="${pageContext.request.contextPath}/users" class="btn btn-sm btn-outline-secondary">Truy cập</a></td>

                            </tr>

                            <tr>

                                <td><strong>CustomerServlet</strong></td>

                                <td><code>/customers</code></td>

                                <td><span class="badge bg-info text-dark">Sprint 2</span></td>

                                <td><code>WEB-INF/views/customer/list.jsp</code></td>

                                <td><a href="${pageContext.request.contextPath}/customers" class="btn btn-sm btn-outline-secondary">Truy cập</a></td>

                            </tr>

                            <tr>

                                <td><strong>ServiceServlet</strong></td>

                                <td><code>/services</code></td>

                                <td><span class="badge bg-info text-dark">Sprint 2</span></td>

                                <td><code>WEB-INF/views/service/list.jsp</code></td>

                                <td><a href="${pageContext.request.contextPath}/services" class="btn btn-sm btn-outline-secondary">Truy cập</a></td>

                            </tr>

                            <tr>

                                <td><strong>BookingServlet</strong></td>

                                <td><code>/bookings</code></td>

                                <td><span class="badge bg-info text-dark">Sprint 4</span></td>

                                <td><code>WEB-INF/views/booking/list.jsp</code></td>

                                <td><a href="${pageContext.request.contextPath}/bookings" class="btn btn-sm btn-outline-secondary">Truy cập</a></td>

                            </tr>

                            <tr>

                                <td><strong>DispatchServlet</strong></td>

                                <td><code>/dispatch</code></td>

                                <td><span class="badge bg-info text-dark">Sprint 5</span></td>

                                <td><code>WEB-INF/views/dispatch/schedule.jsp</code></td>

                                <td><a href="${pageContext.request.contextPath}/dispatch" class="btn btn-sm btn-outline-secondary">Truy cập</a></td>

                            </tr>

                            <tr>

                                <td><strong>ReportServlet</strong></td>

                                <td><code>/reports</code></td>

                                <td><span class="badge bg-info text-dark">Sprint 7</span></td>

                                <td><code>WEB-INF/views/report/dashboard.jsp</code></td>

                                <td><a href="${pageContext.request.contextPath}/reports" class="btn btn-sm btn-outline-secondary">Truy cập</a></td>

                            </tr>

                        </tbody>

                    </table>

                </div>

            </div>

        </div>

    </div>

</main>

<%@ include file="../layout/footer.jspf" %>
