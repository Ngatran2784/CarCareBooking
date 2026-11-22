<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>
<c:set var="pageTitle" value="Khoang & Lịch bảo trì"/><%@ include file="../layout/header.jspf" %>
<%@ include file="../layout/sidebar.jspf" %>
<main class="content-wrapper"><div class="container-fluid">

<div class="mb-4"><h3 class="fw-bold mb-1">Khoang & Lịch bảo trì</h3><p class="text-muted mb-0">Quản lý khoang thi công và chủ động lên lịch bảo dưỡng thiết bị.</p></div>
<c:if test="${not empty error}"><div class="alert alert-danger" role="alert"><c:out value="${error}"/></div></c:if>
<c:if test="${not empty success}"><div class="alert alert-success" role="status"><c:out value="${success}"/></div></c:if>
<c:if test="${ready}">
<%@ include file="form.jsp" %>
<section class="card mb-4"><div class="card-body"><h5 class="mb-3">Danh sách khoang <span class="badge bg-primary">${items.size()}</span></h5><div class="table-responsive"><table class="table align-middle" id="baysTable"><thead><tr><th>Tên khoang / cầu</th><th>Loại khoang</th><th>Trạng thái hiện tại</th><th>Thao tác</th></tr></thead><tbody>
<c:forEach var="r" items="${items}"><tr data-record-id="${r.bay_id}"><td><strong><c:out value="${r.bay_name}"/></strong></td><td><c:out value="${r.bay_type}"/></td><td><span class="badge ${r.effective_status == 'AVAILABLE' ? 'bg-success-subtle text-success' : (r.effective_status == 'OCCUPIED' ? 'bg-primary-subtle text-primary' : 'bg-warning-subtle text-dark')}">${r.effective_status == 'AVAILABLE' ? 'Trống' : (r.effective_status == 'OCCUPIED' ? 'Đang sử dụng' : 'Bảo trì')}</span><c:if test="${r.effective_status != r.status}"><div class="text-muted small">Theo lịch bảo trì</div></c:if></td><td class="text-nowrap"><a class="btn btn-sm btn-outline-primary" href="${ctx}/bays?edit=${r.bay_id}#bayEditor">Sửa</a>
<form class="d-inline catalog-delete" method="post" action="${ctx}/bays"><input type="hidden" name="csrf" value="${sessionScope.catalogCsrf}"><input type="hidden" name="action" value="deleteBay"><input type="hidden" name="id" value="${r.bay_id}"><button class="btn btn-sm btn-outline-danger">Xóa</button></form></td></tr></c:forEach>
<c:if test="${empty items}"><tr><td colspan="4" class="text-center text-muted py-4">Chưa có khoang dịch vụ. Thêm khoang trước khi lập lịch bảo trì.</td></tr></c:if>
</tbody></table></div><p class="text-muted small mb-0">Không thể xóa khoang đang sử dụng hoặc còn phân công/lịch bảo trì.</p></div></section>
<%@ include file="maintenance-form.jsp" %>
<section class="card mb-4"><div class="card-body"><h5 class="mb-3">Lịch bảo trì khoang</h5><div class="table-responsive"><table class="table align-middle" id="maintenanceTable"><thead><tr><th>Khoang</th><th>Bắt đầu</th><th>Kết thúc</th><th>Nội dung</th><th>Trạng thái</th><th>Thao tác</th></tr></thead><tbody>
<c:forEach var="r" items="${details}"><tr data-record-id="${r.maintenance_id}"><td><c:out value="${r.bay_name}"/></td><td class="text-nowrap"><fmt:formatDate value="${r.start_time}" pattern="dd/MM/yyyy HH:mm"/></td><td class="text-nowrap"><fmt:formatDate value="${r.end_time}" pattern="dd/MM/yyyy HH:mm"/></td><td class="text-break"><c:out value="${r.reason}"/></td><td><c:choose><c:when test="${r.status == 'SCHEDULED'}"><span class="badge bg-primary-subtle text-primary">Đã lên lịch</span></c:when><c:when test="${r.status == 'IN_PROGRESS'}"><span class="badge bg-warning-subtle text-dark">Đang bảo trì</span></c:when><c:when test="${r.status == 'COMPLETED'}"><span class="badge bg-success-subtle text-success">Hoàn thành</span></c:when><c:otherwise><span class="badge bg-secondary-subtle text-secondary">Đã hủy</span></c:otherwise></c:choose></td><td class="text-nowrap"><a class="btn btn-sm btn-outline-primary" href="${ctx}/bays?maintenanceEdit=${r.maintenance_id}#maintenanceEditor">Sửa</a>
<form class="d-inline catalog-delete" method="post" action="${ctx}/bays"><input type="hidden" name="csrf" value="${sessionScope.catalogCsrf}"><input type="hidden" name="action" value="deleteMaintenance"><input type="hidden" name="id" value="${r.maintenance_id}"><button class="btn btn-sm btn-outline-danger">Xóa</button></form></td></tr></c:forEach>
<c:if test="${empty details}"><tr><td colspan="6" class="text-center text-muted py-4">Chưa có lịch bảo trì.</td></tr></c:if>
</tbody></table></div></div></section></c:if></div></main><script src="${ctx}/assets/js/catalog.js" defer></script><%@ include file="../layout/footer.jspf" %>