<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Danh Sách Sân — SportZone" scope="request"/>
<c:set var="pageCss" value="court-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-court" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- ==============================================
     COURT LIST (U7) — SPORTZONE CONCEPT SYNC
     ============================================== -->
<div class="container" id="courtListView" style="padding-top:40px;">
    <!-- Hero Section with Synchronized Aesthetics -->
    <div class="court-hero">
        <div class="kicker"><span class="dot"></span> TỔ HỢP ${tennisCount + pickleballCount} SÂN ĐẠT CHUẨN QUỐC TẾ</div>
        <h1>Chọn Sân &amp; <span class="accent-text">Lên Lịch Tranh Tài</span></h1>
        <p>Hệ thống ${tennisCount} sân Tennis mặt cứng chuẩn ITF và ${pickleballCount} sân Pickleball Pro Tour tại TP. Hồ Chí Minh. Mở cửa 06:00 – 21:00 hằng ngày.</p>
    </div>

    <!-- Filter Buttons (Tennis / Pickleball / All) -->
    <div class="filter-bar">
        <a class="filter-btn ${typeFilter == 'all' || empty typeFilter ? 'active' : ''}" href="${ctx}/MainController?action=courts">Tất Cả (${tennisCount + pickleballCount})</a>
        <a class="filter-btn ${typeFilter == 'tennis' ? 'active' : ''}" href="${ctx}/MainController?action=courts&amp;type=tennis">Tennis (${tennisCount})</a>
        <a class="filter-btn ${typeFilter == 'pickleball' ? 'active' : ''}" href="${ctx}/MainController?action=courts&amp;type=pickleball">Pickleball (${pickleballCount})</a>
    </div>

    <!-- Court Cards Grid with Authentic Tennis & Pickleball Imagery -->
    <c:choose>
        <c:when test="${not empty courtList}">
            <div class="court-grid">
                <c:forEach var="c" items="${courtList}">
                    <!-- Exact Tennis & Pickleball Court Images Specified by User -->
                    <c:set var="courtImg">
                        <c:choose>
                            <c:when test="${c.typeSlug == 'tennis' || c.courtId <= 5}">https://t4.ftcdn.net/jpg/09/88/84/25/360_F_988842500_3QxDF8zdI7Tywxak4eZJYeD1piKokUrE.jpg</c:when>
                            <c:otherwise>https://img.magnific.com/premium-photo/pickleball-court-sports-court-with-net-3d-rendering_747516-496.jpg</c:otherwise>
                        </c:choose>
                    </c:set>

                    <article class="court-card ${c.status != 'Available' ? 'is-disabled' : ''}" data-type="${c.typeSlug}">
                        <!-- Court Image Links Directly to Detail -->
                        <a class="court-card-media" href="${ctx}/MainController?action=court&amp;id=${c.courtId}" aria-label="Xem chi tiết sân <c:out value='${c.courtName}'/>">
                            <img src="${courtImg}" alt="<c:out value='${c.courtName}'/>">
                            <div class="court-badge-floating">
                                <span class="sz-badge ${c.typeSlug == 'tennis' ? 'sz-badge-tennis' : 'sz-badge-pickle'}">
                                    <c:out value="${c.typeName}"/>
                                </span>
                                <c:choose>
                                    <c:when test="${c.status == 'Available'}">
                                        <span class="sz-badge sz-badge-available">Trống</span>
                                    </c:when>
                                    <c:when test="${c.status == 'Maintenance'}">
                                        <span class="sz-badge sz-badge-maint">Bảo trì</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="sz-badge sz-badge-full">Không khả dụng</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </a>
                        <div class="court-card-body">
                            <div>
                                <h2 class="court-title">
                                    <a href="${ctx}/MainController?action=court&amp;id=${c.courtId}"><c:out value="${c.courtName}"/></a>
                                </h2>
                                <div class="court-location">
                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/>
                                        <circle cx="12" cy="10" r="3"/>
                                    </svg>
                                    <span><c:out value="${c.location}"/></span>
                                </div>
                                <div class="court-features">
                                    <c:choose>
                                        <c:when test="${c.typeSlug == 'tennis'}">
                                            <span class="feat-tag">Mặt Sân Acrylic</span>
                                            <span class="feat-tag">Đèn LED 500 Lux</span>
                                            <span class="feat-tag">Khung Giờ 06-21h</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="feat-tag">Sân Đa Năng</span>
                                            <span class="feat-tag">Chống Trơn Trượt</span>
                                            <span class="feat-tag">Khung Giờ 06-21h</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            <div class="court-card-foot">
                                <div class="court-price">
                                    <fmt:formatNumber value="${c.pricePerHour}" groupingUsed="true"/> đ <span>/ giờ</span>
                                </div>
                                <c:choose>
                                    <c:when test="${c.status == 'Available'}">
                                        <a class="btn-book-court" href="${ctx}/MainController?action=court&amp;id=${c.courtId}">Đặt Sân Ngay →</a>
                                    </c:when>
                                    <c:when test="${c.status == 'Maintenance'}">
                                        <a class="btn-book-court" href="${ctx}/MainController?action=court&amp;id=${c.courtId}" style="background:#b91c1c; opacity:.75">Bảo Trì →</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a class="btn-book-court" href="${ctx}/MainController?action=court&amp;id=${c.courtId}">Xem Chi Tiết →</a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </article>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="empty-state" style="text-align:center; padding:60px 20px;">
                <span class="sz-badge sz-badge-pickle" style="margin-bottom:14px">KHÔNG CÓ KẾT QUẢ</span>
                <h2 style="font-size:24px; margin-bottom:8px;">Chưa có sân phù hợp</h2>
                <p style="color:var(--muted); margin-bottom:24px;">Hãy thử loại sân khác hoặc quay lại danh sách đầy đủ.</p>
                <a class="btn-book-court" href="${ctx}/MainController?action=courts">Xem tất cả sân</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
