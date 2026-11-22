<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>
<c:set var="pageTitle" value="Dịch vụ & Bảng giá"/><%@ include file="../layout/header.jspf" %>
<%@ include file="../layout/sidebar.jspf" %>
<main class="content-wrapper"><div class="container-fluid">

<div class="mb-4"><h3 class="fw-bold mb-1">Dịch vụ & Bảng giá</h3><p class="text-muted mb-0">Quản lý dịch vụ chăm sóc xe, giá niêm yết và thời lượng thực hiện.</p></div>
<c:if test="${not empty error}"><div class="alert alert-danger" role="alert"><c:out value="${error}"/></div></c:if>
<c:if test="${not empty success}"><div class="alert alert-success" role="status"><c:out value="${success}"/></div></c:if>
<c:if test="${ready}">
<c:if test="${isAdminOrManager}"><%@ include file="form.jsp" %></c:if>
<section class="card mb-4"><div class="card-body">
<div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-3"><h5 class="mb-0">Danh mục dịch vụ <span class="badge bg-primary">${items.size()}</span></h5><div><label class="visually-hidden" for="serviceSearch">Tìm dịch vụ</label><input class="form-control" id="serviceSearch" type="search" placeholder="Tìm tên, mô tả, trạng thái..." data-filter-table="servicesTable"></div></div>
<div class="table-responsive"><table class="table align-middle" id="servicesTable"><thead><tr><th>Dịch vụ</th><th class="text-nowrap">Giá (VNĐ)</th><th>Thời lượng</th><th>Trạng thái</th><c:if test="${isAdminOrManager}"><th>Thao tác</th></c:if></tr></thead><tbody>
<c:forEach var="r" items="${items}"><tr data-record-id="${r.service_id}"><td><strong><c:out value="${r.service_name}"/></strong><div class="text-muted small text-break" style="max-width:480px;white-space:pre-wrap"><c:out value="${r.description}"/></div></td><td class="text-nowrap"><fmt:formatNumber value="${r.base_price}" maxFractionDigits="2"/></td><td class="text-nowrap">${r.estimated_duration_minutes} phút</td><td><span class="badge ${r.status == 'ACTIVE' ? 'bg-success-subtle text-success' : 'bg-secondary-subtle text-secondary'}">${r.status == 'ACTIVE' ? 'Đang cung cấp' : 'Ngừng cung cấp'}</span></td>
<c:if test="${isAdminOrManager}"><td class="text-nowrap"><a class="btn btn-sm btn-outline-primary" href="${ctx}/services?edit=${r.service_id}#serviceEditor">Sửa</a>
<form class="d-inline catalog-delete" method="post" action="${ctx}/services"><input type="hidden" name="csrf" value="${sessionScope.catalogCsrf}"><input type="hidden" name="action" value="deleteService"><input type="hidden" name="id" value="${r.service_id}"><button class="btn btn-sm btn-outline-danger">Xóa</button></form></td></c:if></tr></c:forEach>
<c:if test="${empty items}"><tr><td colspan="5" class="text-center text-muted py-4">Chưa có dịch vụ.</td></tr></c:if>
</tbody></table></div><p class="small text-muted mb-0">Dịch vụ đã được sử dụng hoặc có lịch sử giá cần chuyển sang Ngừng cung cấp thay vì xóa.</p>
</div></section>
<c:if test="${isAdminOrManager}"><section class="card mb-4"><div class="card-body"><h5 class="mb-3">Lịch sử thay đổi giá</h5><div class="table-responsive"><table class="table align-middle" id="pricesTable"><thead><tr><th>Dịch vụ</th><th>Giá cũ (VNĐ)</th><th>Giá mới (VNĐ)</th><th>Thời điểm</th><th>Người thay đổi</th></tr></thead><tbody>
<c:forEach var="r" items="${details}"><tr><td><c:out value="${r.service_name}"/></td><td><fmt:formatNumber value="${r.old_price}" maxFractionDigits="2"/></td><td><fmt:formatNumber value="${r.new_price}" maxFractionDigits="2"/></td><td class="text-nowrap"><fmt:formatDate value="${r.effective_date}" pattern="dd/MM/yyyy HH:mm"/></td><td><c:out value="${empty r.full_name ? 'Hệ thống' : r.full_name}"/></td></tr></c:forEach>
<c:if test="${empty details}"><tr><td colspan="5" class="text-center text-muted py-4">Chưa có thay đổi giá.</td></tr></c:if>
</tbody></table></div></div></section></c:if></c:if></div></main><script src="${ctx}/assets/js/catalog.js" defer></script><%@ include file="../layout/footer.jspf" %>