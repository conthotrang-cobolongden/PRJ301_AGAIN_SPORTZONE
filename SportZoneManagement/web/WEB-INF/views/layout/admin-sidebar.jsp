<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<aside class="admin-sidebar" id="adminSidebar">
    <div class="sidebar-logo"><a class="admin-brand" href="${ctx}/MainController?action=adminDashboard"><svg viewBox="0 0 48 48" aria-hidden="true"><rect x="2" y="2" width="44" height="44" rx="14" fill="#1b5e3b"/><path d="M12 24h24M24 12v24" stroke="#caa54b" stroke-width="1.6"/><path d="M16 14h16a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H16a2 2 0 0 1-2-2V16a2 2 0 0 1 2-2Z" fill="none" stroke="#fff" stroke-width="1.5"/><circle cx="24" cy="24" r="3.5" fill="#caa54b"/></svg><span>Sport<span>Zone</span><small>ADMIN SUITE</small></span></a><button class="sidebar-toggle" id="sidebarToggle" type="button" aria-label="Thu gọn sidebar">☰</button></div>
    <nav class="sidebar-nav">
        <a class="sidebar-link ${adminSection == 'dashboard' ? 'active' : ''}" href="${ctx}/MainController?action=adminDashboard"><span class="nav-icon">▦</span><span class="nav-label">Dashboard</span></a>
        <a class="sidebar-link ${adminSection == 'bookings' ? 'active' : ''}" href="${ctx}/MainController?action=adminBookings"><span class="nav-icon">▣</span><span class="nav-label">Bookings</span></a>
        <a class="sidebar-link ${adminSection == 'courts' ? 'active' : ''}" href="${ctx}/MainController?action=adminCourts"><span class="nav-icon">⌗</span><span class="nav-label">Courts &amp; TimeSlots</span></a>
        <button class="sidebar-link coming" type="button" disabled><span class="nav-icon">▤</span><span class="nav-label">Invoices</span><span class="coming-badge">Coming Soon</span></button>
        <button class="sidebar-link coming" type="button" disabled><span class="nav-icon">♙</span><span class="nav-label">Users</span><span class="coming-badge">Coming Soon</span></button>
        <a class="sidebar-link" href="${ctx}/MainController?action=home"><span class="nav-icon">↗</span><span class="nav-label">Trang khách hàng</span></a>
        <a class="sidebar-link" href="${ctx}/MainController?action=profile"><span class="nav-icon">◎</span><span class="nav-label">Hồ sơ Admin</span></a>
    </nav>
    <div class="sidebar-profile"><div class="profile-avatar">AD</div><div class="profile-info"><strong><c:out value="${sessionScope.account.fullName}"/></strong><span>Quản trị viên</span></div><a class="profile-logout" href="${ctx}/MainController?action=logout" aria-label="Đăng xuất">↗</a></div>
</aside>
