<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Danh sách phương tiện - CarCareBooking" />
<%@ include file="../layout/header.jspf" %>
<%@ include file="../layout/sidebar.jspf" %>

<main class="content-wrapper">
    <div class="container-fluid">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <div>
                <h3 class="fw-bold mb-1">Danh sách phương tiện</h3>
                <span class="badge bg-primary">Sprint 2</span>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-secondary btn-sm">
                    <i class="bi bi-arrow-left"></i> Quay lại Dashboard
                </a>
            </div>
        </div>

        <div class="alert alert-info d-flex align-items-center mb-4" role="alert">
            <i class="bi bi-info-circle-fill fs-4 me-3"></i>
            <div>
                <strong>Thông báo trạng thái phát triển:</strong>
                Chức năng <em>Danh sách phương tiện</em> thuộc phạm vi của <strong>Sprint 2</strong>. Khung giao diện JSP và Servlet Controller đã được khởi tạo hoàn chỉnh và hợp lệ. Mã nguồn nghiệp vụ sẽ được lập trình trong sprint tương ứng theo tài liệu thiết kế.
            </div>
        </div>

        <div class="card">
            <div class="card-header bg-white">
                <i class="bi bi-gear me-2"></i>Mô tả & Yêu cầu chức năng
            </div>
            <div class="card-body">
                <p class="text-secondary mb-3">Quản lý xe ô tô của khách hàng theo biển số và thương hiệu.</p>
                <div class="border rounded p-3 bg-light text-center py-5">
                    <i class="bi bi-cone-striped text-warning display-4 mb-3"></i>
                    <h5>Module đang trong quá trình phát triển (Sprint 2)</h5>
                    <p class="text-muted small">Dữ liệu sẽ được hiển thị khi kết nối CSDL và cài đặt phương thức DAO tương ứng.</p>
                </div>
            </div>
        </div>
    </div>
</main>

<%@ include file="../layout/footer.jspf" %>
