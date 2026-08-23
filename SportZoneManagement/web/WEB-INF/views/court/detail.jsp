<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- U8: Chi tiết sân + U9: khung giờ trống theo ngày — UI theo demo kit C4 --%>
<c:set var="pageTitle" value="${court.courtName} — SportZone" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container sz-section">
    <a class="sz-back-link" href="${ctx}/MainController?action=courts">&larr; Quay lại danh sách sân</a>

    <div class="row g-4">
        <!-- ===== Ảnh + thông tin sân ===== -->
        <div class="col-lg-5">
            <div class="sz-court-chips mb-3">
                <c:choose>
                    <c:when test="${not empty court.imageUrl}">
                        <img class="sz-court-img" style="border-radius:1.25rem" src="<c:out value='${court.imageUrl}'/>" alt="<c:out value='${court.courtName}'/>">
                    </c:when>
                    <c:otherwise>
                        <div class="sz-court-img sz-court-img-ph ${court.typeSlug == 'tennis' ? 'ph-tennis' : 'ph-pickle'}" style="border-radius:1.25rem">
                            <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="10"/><path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/></svg>
                            <span><c:out value="${court.typeName}"/></span>
                        </div>
                    </c:otherwise>
                </c:choose>
                <span class="chip-type"><span class="sz-badge ${court.typeSlug == 'tennis' ? 'sz-badge-tennis' : 'sz-badge-pickle'}"><c:out value="${court.typeName}"/></span></span>
                <span class="chip-status">
                    <c:choose>
                        <c:when test="${court.status == 'Available'}"><span class="sz-badge sz-badge-available">Trống</span></c:when>
                        <c:when test="${court.status == 'Maintenance'}"><span class="sz-badge sz-badge-maintenance">Bảo trì</span></c:when>
                        <c:otherwise><span class="sz-badge sz-badge-unavailable">Không khả dụng</span></c:otherwise>
                    </c:choose>
                </span>
            </div>

            <div class="sz-card">
                <h1 class="h3 mb-1"><c:out value="${court.courtName}"/></h1>
                <p class="sz-meta mb-3">
                    <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/></svg>
                    <c:out value="${court.location}"/>
                </p>
                <c:if test="${not empty court.description}">
                    <p class="small mb-3"><c:out value="${court.description}"/></p>
                </c:if>
                <div class="sz-kv"><dt>Loại sân</dt><dd><c:out value="${court.typeName}"/></dd></div>
                <div class="sz-kv"><dt>Giờ hoạt động</dt><dd>06:00 – 21:00</dd></div>
                <div class="sz-kv"><dt>Giá thuê</dt><dd><fmt:formatNumber value="${court.pricePerHour}" type="number" groupingUsed="true"/> đ / giờ</dd></div>
                <div class="mt-3">
                    <c:choose>
                        <c:when test="${court.status == 'Available'}">
                            <a class="btn btn-sz w-100" href="${ctx}/MainController?action=bookingCreate&amp;courtId=${court.courtId}&amp;date=${selectedDate}">Đặt sân ngày này</a>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-warning mb-0" role="alert">
                                Sân đang ${court.status == 'Maintenance' ? 'bảo trì' : 'tạm ngưng phục vụ'}, vui lòng chọn sân khác.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- ===== U9: CHỌN NGÀY + KHUNG GIỜ (demo kit C4) ===== -->
        <div class="col-lg-7">
            <div class="sz-card h-100">
                <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
                    <h2 class="h5 mb-0">
                        <svg class="me-2" style="color:var(--sz-green);vertical-align:-3px" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                        Chọn ngày &amp; khung giờ
                    </h2>
                    <span class="sz-badge sz-badge-gold"><fmt:formatDate value="${selectedDateObj}" pattern="MM/yyyy"/></span>
                </div>

                <!-- Dãy pill chọn ngày: dateOptions do servlet sinh (hôm nay → +6 ngày) -->
                <div class="date-pill-row mb-4">
                    <c:forEach var="d" items="${dateOptions}">
                        <a class="date-pill ${d.selected ? 'active' : ''}" href="${ctx}/MainController?action=court&amp;id=${court.courtId}&amp;date=${d.value}">
                            <small><c:out value="${d.weekday}"/></small>
                            <b><c:out value="${d.dayMonth}"/></b>
                        </a>
                    </c:forEach>
                </div>

                <c:choose>
                    <c:when test="${not empty slotList}">
                        <!-- slotList: List<TimeSlot> theo court+date; s.past do servlet đánh dấu (U9) -->
                        <div class="sz-slot-grid">
                            <c:forEach var="s" items="${slotList}">
                                <c:choose>
                                    <c:when test="${s.status == 'Available' && court.status == 'Available' && !s.past}">
                                        <a class="sz-slot is-available" href="${ctx}/MainController?action=bookingCreate&amp;courtId=${court.courtId}&amp;date=${selectedDate}&amp;slotId=${s.timeSlotId}">
                                            <span class="sz-slot-time"><c:out value="${s.startTime}"/></span>
                                            <span class="sz-slot-label"><fmt:formatNumber value="${court.pricePerHour}" type="number" groupingUsed="true"/> đ</span>
                                        </a>
                                    </c:when>
                                    <c:when test="${s.past}">
                                        <span class="sz-slot is-past">
                                            <span class="sz-slot-time"><c:out value="${s.startTime}"/></span>
                                            <span class="sz-slot-label">Đã qua</span>
                                        </span>
                                    </c:when>
                                    <c:when test="${s.status == 'Booked'}">
                                        <span class="sz-slot is-booked">
                                            <span class="sz-slot-time"><c:out value="${s.startTime}"/></span>
                                            <span class="sz-slot-label">Đã đặt</span>
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="sz-slot is-closed">
                                            <span class="sz-slot-time"><c:out value="${s.startTime}"/></span>
                                            <span class="sz-slot-label">Đóng</span>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </div>
                        <div class="sz-slot-legend">
                            <span><span class="swatch swatch-available"></span>Trống — bấm để đặt</span>
                            <span><span class="swatch swatch-booked"></span>Đã đặt</span>
                            <span><span class="swatch swatch-closed"></span>Đóng / Đã qua</span>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="sz-empty">
                            <div class="icon-circle">
                                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                            </div>
                            <h2>Chưa mở khung giờ ngày này</h2>
                            <p class="mb-0">Vui lòng chọn ngày khác trong tuần.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
