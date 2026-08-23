<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="${court.courtName} — SportZone" scope="request"/>
<c:set var="pageCss" value="court-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-court" scope="request"/>
<c:set var="headerVariant" value="standard" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="container detail-container" id="courtDetailView">
    <div class="back-btn-row"><a class="btn-back" href="${ctx}/MainController?action=courts"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m15 18-6-6 6-6"/></svg><span>Quay lại danh sách tất cả sân</span></a></div>
    <div class="detail-grid">
        <div class="detail-card">
            <div class="detail-media"><c:choose><c:when test="${not empty court.imageUrl}"><img src="<c:out value='${court.imageUrl}'/>" alt="<c:out value='${court.courtName}'/>"></c:when><c:otherwise><div class="court-media-placeholder ${court.typeSlug}"><span><c:out value="${court.typeName}"/></span></div></c:otherwise></c:choose></div>
            <div class="detail-badges"><span class="sz-badge ${court.typeSlug == 'tennis' ? 'sz-badge-tennis' : 'sz-badge-pickle'}"><c:out value="${court.typeName}"/></span><c:choose><c:when test="${court.status == 'Available'}"><span class="sz-badge sz-badge-available">Sẵn Sàng</span></c:when><c:when test="${court.status == 'Maintenance'}"><span class="sz-badge sz-badge-maint">Bảo Trì</span></c:when><c:otherwise><span class="sz-badge sz-badge-full">Không Khả Dụng</span></c:otherwise></c:choose></div>
            <h1><c:out value="${court.courtName}"/></h1><p class="detail-description"><c:out value="${court.description}"/></p>
            <div class="spec-row"><span class="spec-label">Vị trí</span><span class="spec-val"><c:out value="${court.location}"/></span></div><div class="spec-row"><span class="spec-label">Loại sân</span><span class="spec-val"><c:out value="${court.typeName}"/></span></div><div class="spec-row"><span class="spec-label">Giờ mở cửa</span><span class="spec-val">06:00 – 21:00 hằng ngày</span></div><div class="spec-row"><span class="spec-label">Giá thuê tiêu chuẩn</span><span class="spec-val" style="color:var(--green)"><fmt:formatNumber value="${court.pricePerHour}" groupingUsed="true"/> đ / giờ</span></div>
            <c:choose><c:when test="${court.status == 'Available'}"><a class="btn-book-court detail-action" href="${ctx}/MainController?action=bookingCreate&amp;courtId=${court.courtId}&amp;date=${selectedDate}">Tiến Hành Đặt Khung Giờ Đã Chọn</a></c:when><c:otherwise><button class="btn-book-court detail-action" type="button" disabled>Sân Tạm Ngưng Phục Vụ</button></c:otherwise></c:choose>
        </div>
        <div class="detail-card">
            <div class="detail-section-head"><h2>Chọn Ngày &amp; Khung Giờ</h2><span class="sz-badge sz-badge-pickle"><fmt:formatDate value="${selectedDateObj}" pattern="MM/yyyy"/></span></div>
            <div class="date-scroller"><c:forEach var="d" items="${dateOptions}"><a class="date-pill ${d.selected ? 'active' : ''}" href="${ctx}/MainController?action=court&amp;id=${court.courtId}&amp;date=${d.value}"><div class="day-name"><c:out value="${d.weekday}"/></div><div class="day-num"><c:out value="${d.dayMonth}"/></div></a></c:forEach></div>
            <c:choose><c:when test="${not empty slotList}"><div class="slot-matrix"><c:forEach var="s" items="${slotList}"><c:choose><c:when test="${s.status == 'Available' && court.status == 'Available' && !s.past}"><a class="slot-card available" href="${ctx}/MainController?action=bookingCreate&amp;courtId=${court.courtId}&amp;date=${selectedDate}&amp;slotId=${s.timeSlotId}"><div class="slot-time"><c:out value="${s.startTime}"/> – <c:out value="${s.endTime}"/></div><div class="slot-price"><fmt:formatNumber value="${court.pricePerHour}" groupingUsed="true"/> đ</div></a></c:when><c:when test="${s.past}"><div class="slot-card past"><div class="slot-time"><c:out value="${s.startTime}"/> – <c:out value="${s.endTime}"/></div><div class="slot-price">Đã qua</div></div></c:when><c:when test="${s.status == 'Booked'}"><div class="slot-card booked"><div class="slot-time"><c:out value="${s.startTime}"/> – <c:out value="${s.endTime}"/></div><div class="slot-price">Đã đặt</div></div></c:when><c:otherwise><div class="slot-card closed"><div class="slot-time"><c:out value="${s.startTime}"/> – <c:out value="${s.endTime}"/></div><div class="slot-price">Đóng</div></div></c:otherwise></c:choose></c:forEach></div><div class="slot-legend"><span><i class="legend-dot available"></i>Trống</span><span><i class="legend-dot booked"></i>Đã đặt</span><span><i class="legend-dot past"></i>Đã qua / đóng</span></div></c:when><c:otherwise><div class="empty-state"><h2>Chưa mở khung giờ ngày này</h2><p>Vui lòng chọn ngày khác trong tuần.</p></div></c:otherwise></c:choose>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
