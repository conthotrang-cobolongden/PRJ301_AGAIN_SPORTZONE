<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Danh sách sân — SportZone" scope="request"/>
<c:set var="pageCss" value="court-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-court" scope="request"/>
<c:set var="headerVariant" value="standard" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="container" id="courtListView">
    <div class="court-hero"><span class="sz-badge sz-badge-tennis" style="margin-bottom:12px">TỔ HỢP SÂN ĐẠT CHUẨN QUỐC TẾ</span><h1>Chọn Sân &amp; Lên Lịch Tranh Tài</h1><p>${tennisCount + pickleballCount} sân đang hoạt động tại Hòa Lạc. Mở cửa 06:00 – 21:00 hằng ngày.</p></div>
    <div class="filter-bar"><a class="filter-btn ${typeFilter == 'all' || empty typeFilter ? 'active' : ''}" href="${ctx}/MainController?action=courts">Tất Cả (${tennisCount + pickleballCount})</a><a class="filter-btn ${typeFilter == 'tennis' ? 'active' : ''}" href="${ctx}/MainController?action=courts&amp;type=tennis">Tennis (${tennisCount})</a><a class="filter-btn ${typeFilter == 'pickleball' ? 'active' : ''}" href="${ctx}/MainController?action=courts&amp;type=pickleball">Pickleball (${pickleballCount})</a></div>
    <c:choose><c:when test="${not empty courtList}"><div class="court-grid"><c:forEach var="c" items="${courtList}">
        <article class="court-card ${c.status != 'Available' ? 'is-disabled' : ''}" data-type="${c.typeSlug}">
            <div class="court-card-media"><c:choose><c:when test="${not empty c.imageUrl}"><img src="<c:out value='${c.imageUrl}'/>" alt="<c:out value='${c.courtName}'/>"></c:when><c:otherwise><div class="court-media-placeholder ${c.typeSlug}"><span><c:out value="${c.typeName}"/></span></div></c:otherwise></c:choose><div class="court-badge-floating"><span class="sz-badge ${c.typeSlug == 'tennis' ? 'sz-badge-tennis' : 'sz-badge-pickle'}"><c:out value="${c.typeName}"/></span><c:choose><c:when test="${c.status == 'Available'}"><span class="sz-badge sz-badge-available">Trống</span></c:when><c:when test="${c.status == 'Maintenance'}"><span class="sz-badge sz-badge-maint">Bảo trì</span></c:when><c:otherwise><span class="sz-badge sz-badge-full">Không khả dụng</span></c:otherwise></c:choose></div></div>
            <div class="court-card-body"><div><h2 class="court-title"><c:out value="${c.courtName}"/></h2><div class="court-location"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/></svg><span><c:out value="${c.location}"/></span></div><div class="court-features"><span class="feat-tag">Mặt sân Acrylic</span><span class="feat-tag">Đèn LED chống lóa</span><span class="feat-tag">Mở cửa 06:00 – 21:00</span></div></div><div class="court-card-foot"><div class="court-price"><fmt:formatNumber value="${c.pricePerHour}" groupingUsed="true"/> đ <span>/ giờ</span></div><a class="btn-book-court" href="${ctx}/MainController?action=court&amp;id=${c.courtId}">${c.status == 'Available' ? 'Đặt Sân' : 'Xem Chi Tiết'}</a></div></div>
        </article>
    </c:forEach></div></c:when><c:otherwise><div class="empty-state"><span class="sz-badge sz-badge-pickle">KHÔNG CÓ KẾT QUẢ</span><h2>Chưa có sân phù hợp</h2><p>Hãy thử loại sân khác hoặc quay lại danh sách đầy đủ.</p><a class="btn-book-court" href="${ctx}/MainController?action=courts">Xem tất cả sân</a></div></c:otherwise></c:choose>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
