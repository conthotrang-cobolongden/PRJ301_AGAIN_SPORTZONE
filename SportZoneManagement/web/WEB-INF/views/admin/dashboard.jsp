<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Dashboard Admin — SportZone" scope="request"/>
<c:set var="pageCss" value="admin-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-admin" scope="request"/>
<c:set var="headerVariant" value="none" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<c:set var="adminSection" value="dashboard" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="admin-layout">
    <jsp:include page="/WEB-INF/views/layout/admin-sidebar.jsp"/>
    <div class="admin-content">
        <header class="admin-topbar"><button class="topbar-toggle" id="topbarToggle" type="button">☰</button><div class="topbar-title">Admin Executive Suite</div><div class="topbar-actions"><span class="system-live"><i></i> Hệ thống hoạt động</span><a href="${ctx}/MainController?action=bookingCreate" class="btn-admin-primary">+ Booking Vãng Lai</a></div></header>
        <main class="admin-main-scroll"><section class="admin-view active">
            <div class="admin-pagehead"><div><span class="page-eyebrow">SPORTZONE CONTROL CENTER</span><h1>Bảng Điều Hành SportZone</h1><p>Tổng quan dữ liệu vận hành toàn hệ thống.</p></div><div class="page-actions"><a class="btn-admin-secondary" href="${ctx}/MainController?action=adminCourts">Quản Lý Sân</a><a class="btn-admin-primary" href="${ctx}/MainController?action=adminBookings">Xem Bookings</a></div></div>
            <div class="kpi-grid">
                <article class="kpi-card"><span class="kpi-icon green">♙</span><span class="lbl">TỔNG TÀI KHOẢN</span><strong class="val">${dashboard.totalAccounts}</strong><small>Dữ liệu người dùng hệ thống</small></article>
                <article class="kpi-card"><span class="kpi-icon gold">⌗</span><span class="lbl">SÂN ĐANG HOẠT ĐỘNG</span><strong class="val">${dashboard.totalCourts}</strong><small>Tennis &amp; Pickleball</small></article>
                <article class="kpi-card"><span class="kpi-icon blue">▣</span><span class="lbl">TỔNG BOOKING</span><strong class="val">${dashboard.totalBookings}</strong><small>Toàn bộ lịch đặt sân</small></article>
                <article class="kpi-card"><span class="kpi-icon green">₫</span><span class="lbl">TỔNG DOANH THU PAID</span><strong class="val"><fmt:formatNumber value="${dashboard.totalRevenue}" groupingUsed="true"/> đ</strong><small>Hóa đơn đã thanh toán</small></article>
                <article class="kpi-card coming-card"><span class="coming-badge">Coming Soon</span><span class="lbl">DOANH THU HÔM NAY</span><strong class="val">—</strong><small>Chưa có backend báo cáo ngày</small></article>
                <article class="kpi-card coming-card"><span class="coming-badge">Coming Soon</span><span class="lbl">TỶ LỆ LẤP ĐẦY</span><strong class="val">—</strong><small>Chưa có backend phân tích slot</small></article>
            </div>
            <div class="dashboard-grid"><section class="admin-panel"><div class="panel-head"><div><h2>Trạng Thái Sân Trực Tiếp</h2><p>Ma trận vận hành 10 sân</p></div><span class="coming-badge">Coming Soon</span></div><div class="coming-panel"><div class="coming-visual court-matrix-placeholder"></div><h3>Live Court Matrix</h3><p>Giao diện đã sẵn sàng, dữ liệu thời gian thực đang được khóa.</p><button type="button" disabled>Xem trạng thái sân</button></div></section><section class="admin-panel"><div class="panel-head"><div><h2>Booking Gần Đây</h2><p>Cập nhật lịch đặt sân mới nhất</p></div><span class="coming-badge">Coming Soon</span></div><div class="coming-panel"><div class="coming-visual table-placeholder"></div><h3>Recent Bookings</h3><p>Dashboard chưa có backend danh sách booking gần đây.</p><a class="btn-admin-secondary" href="${ctx}/MainController?action=adminBookings">Mở danh sách booking</a></div></section></div>
            <section class="admin-panel distribution-panel"><div class="panel-head"><div><h2>Phân Bổ Loại Sân</h2><p>Tennis và Pickleball</p></div><span class="coming-badge">Coming Soon</span></div><div class="coming-panel compact"><p>Biểu đồ phân bổ sẽ hoạt động khi dashboard có dữ liệu theo loại sân.</p><button type="button" disabled>Xem báo cáo phân bổ</button></div></section>
        </section></main>
    </div>
</div>
<script>(function(){var sidebar=document.getElementById('adminSidebar');function toggle(){sidebar.classList.toggle('collapsed');document.body.classList.toggle('sidebar-collapsed');}document.getElementById('sidebarToggle').addEventListener('click',toggle);document.getElementById('topbarToggle').addEventListener('click',toggle);})();</script>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
