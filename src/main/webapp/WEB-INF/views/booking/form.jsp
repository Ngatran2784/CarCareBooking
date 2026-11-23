<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Chọn xe chăm sóc - CarCareBooking" />
<%@ include file="../layout/header.jspf" %>
<%@ include file="../layout/sidebar.jspf" %>
<main class="content-wrapper workspace-page"><div class="container-fluid">
<div class="page-eyebrow">Chăm sóc xe của bạn</div>
<div class="page-hero"><div><h3 class="fw-bold mb-1">Hôm nay, bạn muốn chăm sóc xe nào?</h3><p>Chọn phương tiện đã đăng ký. Kiểm tra biển số và thông tin xe trước khi xác nhận.</p></div><a href="${pageContext.request.contextPath}/vehicles?action=add" class="btn btn-outline-primary"><i class="bi bi-plus-lg me-2"></i>Thêm xe mới</a></div>
<c:if test="${not empty selectedVehicle}"><div class="alert alert-success" role="status"><i class="bi bi-check-circle me-2"></i>Đã chọn xe <strong><c:out value="${selectedVehicle.licensePlate}"/></strong>. Bạn có thể chọn lại xe bên dưới. Lịch hẹn chưa được tạo.</div></c:if>
<form method="get" action="${pageContext.request.contextPath}/bookings" id="vehicleSelectionForm"><input type="hidden" name="action" value="create">
<div class="vehicle-layout"><section aria-labelledby="vehiclesHeading">
<div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4"><h5 id="vehiclesHeading" class="mb-0">Phương tiện đã đăng ký <span class="badge bg-white text-secondary border">${vehicleList.size()}</span></h5><div class="search-field"><i class="bi bi-search"></i><label for="vehicleSearch" class="visually-hidden">Tìm xe</label><input id="vehicleSearch" class="form-control" type="search" placeholder="Tìm biển số, hãng xe, chủ xe..."></div></div>
<fieldset><legend class="visually-hidden">Chọn một xe để chăm sóc</legend><div class="vehicle-grid" id="vehicleOptions">
<c:forEach var="v" items="${vehicleList}"><label class="vehicle-option"><input type="radio" name="vehicleId" value="${v.vehicleId}" required ${selectedVehicle.vehicleId == v.vehicleId ? 'checked' : ''} data-name="<c:out value='${v.brand} ${v.model}'/>" data-plate="<c:out value='${v.licensePlate}'/>">
<div class="vehicle-tile"><div class="vehicle-art" aria-hidden="true"><i class="bi bi-car-front"></i></div><span class="badge bg-secondary-subtle text-secondary"><c:out value="${v.vehicleType}"/></span><h5><c:out value="${v.brand}"/> <c:out value="${v.model}"/></h5><span class="plate"><c:out value="${v.licensePlate}"/></span><p class="vehicle-meta"><c:out value="${empty v.color ? 'Chưa cập nhật màu' : v.color}"/> · <c:out value="${empty v.year ? 'Chưa cập nhật năm' : v.year}"/></p><c:if test="${isFrontDesk}"><p class="vehicle-meta"><i class="bi bi-person me-1"></i><c:out value="${v.customerName}"/></p></c:if></div></label></c:forEach>
</div></fieldset><p id="vehicleNoResults" class="empty-result" hidden>Không tìm thấy xe phù hợp. Hãy thử biển số hoặc hãng xe khác.</p>
<c:if test="${empty vehicleList}"><div class="card"><div class="card-body text-center py-5"><i class="bi bi-car-front fs-1 text-secondary"></i><h5 class="mt-3">Chưa có phương tiện</h5><p class="text-muted">Thêm xe đầu tiên để bắt đầu chăm sóc xe cùng CarCare.</p><a class="btn btn-primary" href="${pageContext.request.contextPath}/vehicles?action=add">Thêm xe mới</a></div></div></c:if>
</section><aside class="selection-summary" aria-labelledby="selectionHeading"><i class="bi bi-check2-circle fs-2" aria-hidden="true"></i><h5 id="selectionHeading" class="mt-3">Xe bạn đã chọn</h5><div aria-live="polite"><p id="selectedVehicleName">Chưa chọn phương tiện</p><strong id="selectedVehiclePlate"></strong></div><hr><p>Hãy kiểm tra đúng xe và biển số để việc tiếp nhận tại xưởng thuận tiện hơn.</p><button class="btn btn-primary" id="confirmVehicle" type="submit" disabled>Xác nhận xe</button><p class="small mt-3 mb-0">Bước này xác nhận phương tiện, chưa tạo lịch hẹn.</p></aside></div></form>
<a href="${pageContext.request.contextPath}/vehicles" class="btn btn-outline-secondary mt-4">Quản lý phương tiện</a>
</div></main><script src="${pageContext.request.contextPath}/assets/js/vehicle-selection.js" defer></script>
<%@ include file="../layout/footer.jspf" %>
