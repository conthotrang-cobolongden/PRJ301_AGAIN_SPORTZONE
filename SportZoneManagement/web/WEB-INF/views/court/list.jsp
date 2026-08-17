<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- U7: Danh sách sân (filter Tennis / Pickleball) — UI theo demo kit C3 --%>
<c:set var="pageTitle" value="Danh sách sân — SportZone" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container sz-section">
    <!-- ===== PAGEHEAD + FILTER PILLS (demo kit C3) ===== -->
    <div class="sz-pagehead">
        <div>
            <p class="section-label mb-1">Danh sách sân</p>
            <h1>Chọn sân của bạn</h1>
            <p class="sz-lead-sm">${tennisCount + pickleballCount} sân đang hoạt động · mở cửa 06:00 – 21:00 hằng ngày.</p>
        </div>
        <!-- TABS lọc loại sân: GET ?type=all|tennis|pickleball -->
        <ul class="nav nav-pills sz-tabs">
            <li class="nav-item">
                <a class="nav-link ${typeFilter == 'all' || empty typeFilter ? 'active' : ''}" href="${ctx}/courts">
                    Tất cả
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${typeFilter == 'tennis' ? 'active' : ''}" href="${ctx}/courts?type=tennis">
                    Tennis <span class="sz-count">${tennisCount}</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${typeFilter == 'pickleball' ? 'active' : ''}" href="${ctx}/courts?type=pickleball">
                    Pickleball <span class="sz-count">${pickleballCount}</span>
                </a>
            </li>
        </ul>
    </div>

    <!-- courtList: List<Court> đã JOIN CourtTypes (typeName/typeSlug từ CourtDAO) -->
    <c:choose>
        <c:when test="${not empty courtList}">
            <div class="row g-4">
                <c:forEach var="c" items="${courtList}">
                    <div class="col-lg-4 col-md-6">
                        <article class="sz-card sz-court-card hover-lift ${c.status != 'Available' ? 'sz-court-off' : ''}">
                            <!-- Ảnh sân; ImageURL null → placeholder gradient theo loại (demo kit C3) -->
                            <div class="sz-court-chips">
                                <c:choose>
                                    <c:when test="${not empty c.imageUrl}">
                                        <img class="sz-court-img" src="<c:out value='${c.imageUrl}'/>" alt="<c:out value='${c.courtName}'/>">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="sz-court-img sz-court-img-ph ${c.typeSlug == 'tennis' ? 'ph-tennis' : 'ph-pickle'}">
                                            <svg xmlns="http://www.w3.org/2000/svg" width="44" height="44" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="10"/><path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/></svg>
                                            <span><c:out value="${c.typeName}"/></span>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                <span class="chip-type"><span class="sz-badge ${c.typeSlug == 'tennis' ? 'sz-badge-tennis' : 'sz-badge-pickle'}"><c:out value="${c.typeName}"/></span></span>
                                <span class="chip-status">
                                    <c:choose>
                                        <c:when test="${c.status == 'Available'}"><span class="sz-badge sz-badge-available">Trống</span></c:when>
                                        <c:when test="${c.status == 'Maintenance'}"><span class="sz-badge sz-badge-maintenance">Bảo trì</span></c:when>
                                        <c:otherwise><span class="sz-badge sz-badge-unavailable">Không khả dụng</span></c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                            <div class="sz-court-body">
                                <h2 class="h5 mb-1"><c:out value="${c.courtName}"/></h2>
                                <p class="sz-meta mb-3">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/></svg>
                                    <c:out value="${c.location}"/>
                                </p>
                                <div class="d-flex justify-content-between align-items-center">
                                    <span class="sz-price mb-0"><fmt:formatNumber value="${c.pricePerHour}" type="number" groupingUsed="true"/> đ<span class="small fw-normal" style="color:var(--sz-muted-ink)"> / giờ</span></span>
                                    <!-- Sân Maintenance/Unavailable: chỉ xem chi tiết, không đặt (spec courts.md) -->
                                    <c:choose>
                                        <c:when test="${c.status == 'Available'}">
                                            <a class="btn btn-sz btn-sm" href="${ctx}/court?id=${c.courtId}">Đặt sân</a>
                                        </c:when>
                                        <c:otherwise>
                                            <a class="btn btn-sz-outline btn-sm" href="${ctx}/court?id=${c.courtId}">Xem chi tiết</a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </article>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <!-- ===== EMPTY STATE (demo kit P0) ===== -->
            <div class="sz-empty">
                <div class="icon-circle">
                    <svg xmlns="http://www.w3.org/2000/svg" width="34" height="34" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="10"/><path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/></svg>
                </div>
                <h2>Không có sân phù hợp</h2>
                <p>Thử chọn lại loại sân khác.</p>
                <a class="btn btn-sz" href="${ctx}/courts">Xem tất cả sân</a>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
