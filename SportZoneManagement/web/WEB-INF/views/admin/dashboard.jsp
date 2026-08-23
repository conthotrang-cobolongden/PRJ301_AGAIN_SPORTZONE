<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Dashboard Admin — SportZone" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<section class="container sz-section">
    <div class="sz-pagehead"><div><p class="sz-kicker sz-kicker-dark mb-1">Admin MVP</p><h1>Dashboard</h1></div></div>
    <div class="row g-3 mb-4">
        <div class="col-6 col-lg-3"><div class="sz-card h-100"><p class="text-muted mb-1">Tài khoản</p><p class="display-6 fw-bold mb-0">${dashboard.totalAccounts}</p></div></div>
        <div class="col-6 col-lg-3"><div class="sz-card h-100"><p class="text-muted mb-1">Sân active</p><p class="display-6 fw-bold mb-0">${dashboard.totalCourts}</p></div></div>
        <div class="col-6 col-lg-3"><div class="sz-card h-100"><p class="text-muted mb-1">Booking</p><p class="display-6 fw-bold mb-0">${dashboard.totalBookings}</p></div></div>
        <div class="col-6 col-lg-3"><div class="sz-card h-100"><p class="text-muted mb-1">Tổng doanh thu Paid</p><p class="h3 fw-bold mb-0"><fmt:formatNumber value="${dashboard.totalRevenue}" groupingUsed="true"/> đ</p></div></div>
    </div>
    <div class="d-flex flex-wrap gap-2">
        <a class="btn btn-sz" href="${ctx}/MainController?action=adminCourts">Quản lý sân và sinh lịch</a>
        <a class="btn btn-sz-outline" href="${ctx}/MainController?action=adminBookings">Danh sách booking</a>
        <a class="btn btn-sz-outline" href="${ctx}/MainController?action=bookingCreate">Tạo booking vãng lai</a>
    </div>
</section>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
