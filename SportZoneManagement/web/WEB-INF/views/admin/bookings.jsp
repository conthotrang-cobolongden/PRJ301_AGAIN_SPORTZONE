<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Booking Admin — SportZone" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<section class="container sz-section">
    <c:if test="${not empty success}"><div class="alert alert-success"><c:out value="${success}"/></div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>
    <c:choose>
        <c:when test="${empty booking}">
            <div class="sz-pagehead"><div><p class="sz-kicker sz-kicker-dark mb-1">Admin MVP</p><h1>Danh sách booking</h1></div><a class="btn btn-sz" href="${ctx}/MainController?action=bookingCreate">Tạo booking vãng lai</a></div>
            <form method="get" action="${ctx}/MainController" class="d-flex gap-2 mb-3"><input type="hidden" name="action" value="adminBookings"/><select class="form-select" style="max-width:260px" name="status"><option value="all">Tất cả trạng thái</option><option value="Pending" ${selectedStatus == 'Pending' ? 'selected' : ''}>Pending</option><option value="Confirmed" ${selectedStatus == 'Confirmed' ? 'selected' : ''}>Confirmed</option><option value="CheckedIn" ${selectedStatus == 'CheckedIn' ? 'selected' : ''}>CheckedIn</option><option value="Completed" ${selectedStatus == 'Completed' ? 'selected' : ''}>Completed</option><option value="Cancelled" ${selectedStatus == 'Cancelled' ? 'selected' : ''}>Cancelled</option><option value="NoShow" ${selectedStatus == 'NoShow' ? 'selected' : ''}>NoShow</option></select><button class="btn btn-sz-outline">Lọc</button></form>
            <div class="table-responsive sz-card p-0"><table class="table align-middle mb-0"><thead><tr><th>Mã</th><th>Khách</th><th>Sân</th><th>Thời gian</th><th>Tổng tiền</th><th>Trạng thái</th><th></th></tr></thead><tbody>
                <c:forEach var="item" items="${bookingList}"><tr><td>#${item.bookingId}</td><td><c:out value="${item.accountName}"/></td><td><c:out value="${item.courtName}"/></td><td><fmt:formatDate value="${item.slotDateValue}" pattern="dd/MM/yyyy"/> ${item.startTime}</td><td><fmt:formatNumber value="${item.totalAmount}" groupingUsed="true"/> đ</td><td><span class="badge text-bg-secondary">${item.status}</span></td><td><a class="btn btn-sm btn-sz-outline" href="${ctx}/MainController?action=adminBookings&amp;id=${item.bookingId}">Chi tiết</a></td></tr></c:forEach>
                <c:if test="${empty bookingList}"><tr><td colspan="7" class="text-center py-4 text-muted">Không có booking.</td></tr></c:if>
            </tbody></table></div>
        </c:when>
        <c:otherwise>
            <a class="sz-back-link" href="${ctx}/MainController?action=adminBookings">&larr; Về danh sách booking</a>
            <div class="sz-pagehead mt-3"><div><p class="sz-kicker sz-kicker-dark mb-1">Booking #${booking.bookingId}</p><h1><c:out value="${booking.accountName}"/></h1></div><span class="badge text-bg-secondary fs-6">${booking.status}</span></div>
            <div class="row g-4"><div class="col-lg-7"><div class="sz-card"><dl class="mb-0">
                <div class="sz-kv"><dt>Khách</dt><dd><c:out value="${booking.accountName}"/> <c:if test="${booking.accountRoleName == 'Admin'}"><small class="text-muted">(Khách vãng lai)</small></c:if></dd></div>
                <div class="sz-kv"><dt>Sân</dt><dd><c:out value="${booking.courtName}"/></dd></div>
                <div class="sz-kv"><dt>Buổi chơi</dt><dd><fmt:formatDate value="${booking.slotDateValue}" pattern="dd/MM/yyyy"/> · ${booking.startTime}–${booking.endTime}</dd></div>
                <div class="sz-kv"><dt>Tổng tiền</dt><dd><fmt:formatNumber value="${booking.totalAmount}" groupingUsed="true"/> đ</dd></div>
                <div class="sz-kv"><dt>Thanh toán</dt><dd>${empty booking.paymentStatus ? 'Chưa có hóa đơn' : booking.paymentStatus}</dd></div>
                <c:if test="${not empty booking.checkInAt}"><div class="sz-kv"><dt>Check-in</dt><dd>${booking.checkInAt}</dd></div></c:if>
            </dl></div></div>
            <div class="col-lg-5"><div class="sz-card"><h2 class="h5">Thao tác</h2>
                <c:if test="${canCheckIn}"><form method="post" action="${ctx}/MainController?action=adminBookings"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/><input type="hidden" name="operation" value="checkIn"/><input type="hidden" name="bookingId" value="${booking.bookingId}"/><button class="btn btn-sz w-100" type="submit">Check-in khách</button></form></c:if>
                <c:if test="${booking.accountRoleName == 'Admin' && booking.accountId == sessionScope.account.accountID}"><div class="d-grid mt-2"><c:choose><c:when test="${booking.paymentStatus == 'Paid'}"><a class="btn btn-sz-outline" href="${ctx}/MainController?action=invoiceDetail&amp;bookingId=${booking.bookingId}">Xem hóa đơn</a></c:when><c:when test="${booking.paymentStatus != 'Cancelled'}"><a class="btn btn-sz-outline" href="${ctx}/MainController?action=invoicePayment&amp;bookingId=${booking.bookingId}">Thanh toán QR</a></c:when></c:choose></div></c:if>
                <c:if test="${!canCheckIn}"><p class="text-muted mb-0 mt-2">Check-in chỉ mở từ 30 phút trước đến trước 30 phút sau giờ bắt đầu.</p></c:if>
            </div></div></div>
        </c:otherwise>
    </c:choose>
</section>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
