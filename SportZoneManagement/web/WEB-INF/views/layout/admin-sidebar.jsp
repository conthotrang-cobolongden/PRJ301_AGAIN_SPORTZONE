<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- ==============================================
     ADMIN SIDEBAR — CHATGPT STYLE MODERN DESIGN
     ============================================== -->
<aside class="admin-sidebar" id="adminSidebar">
    <!-- Brand Header -->
    <div class="sidebar-brand">
        <a class="brand-title" href="${ctx}/MainController?action=adminDashboard">
            <svg viewBox="0 0 48 48" aria-hidden="true">
                <rect x="2" y="2" width="44" height="44" rx="14" fill="#0b1911"/>
                <path d="M12 24h24M24 12v24" stroke="#caa54b" stroke-width="1.6"/>
                <path d="M16 14h16a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H16a2 2 0 0 1-2-2V16a2 2 0 0 1 2-2Z" fill="none" stroke="#fff" stroke-width="1.5"/>
                <circle cx="24" cy="24" r="3.5" fill="#1b5e3b"/>
            </svg>
            <span>Sport<span class="zone">Zone</span></span>
        </a>
        <button class="btn-chatgpt-toggle" id="sidebarToggle" type="button" aria-label="Thu gọn sidebar" title="Thu gọn/Mở rộng">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                <line x1="9" y1="3" x2="9" y2="21"/>
            </svg>
        </button>
    </div>

    <!-- Navigation Menu -->
    <nav class="sidebar-nav">
        <div class="nav-group-label">QUẢN TRỊ VẬN HÀNH</div>
        
        <a class="admin-nav-item ${adminSection == 'dashboard' ? 'active' : ''}" href="${ctx}/MainController?action=adminDashboard">
            <span class="nav-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="3" width="7" height="7"/>
                    <rect x="14" y="3" width="7" height="7"/>
                    <rect x="14" y="14" width="7" height="7"/>
                    <rect x="3" y="14" width="7" height="7"/>
                </svg>
            </span>
            <span class="nav-label">Executive Dashboard</span>
        </a>

        <a class="admin-nav-item ${adminSection == 'bookings' ? 'active' : ''}" href="${ctx}/MainController?action=adminBookings">
            <span class="nav-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                    <line x1="16" y1="2" x2="16" y2="6"/>
                    <line x1="8" y1="2" x2="8" y2="6"/>
                    <line x1="3" y1="10" x2="21" y2="10"/>
                </svg>
            </span>
            <span class="nav-label">Quản Lý Bookings</span>
        </a>

        <a class="admin-nav-item ${adminSection == 'courts' ? 'active' : ''}" href="${ctx}/MainController?action=adminCourts">
            <span class="nav-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1z"/>
                    <line x1="4" y1="22" x2="4" y2="15"/>
                </svg>
            </span>
            <span class="nav-label">Sân &amp; Khung Giờ</span>
        </a>

        <div class="nav-group-label" style="margin-top:12px;">ĐIỀU HƯỚNG NHANH</div>

        <a class="admin-nav-item" href="${ctx}/MainController?action=home">
            <span class="nav-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                    <polyline points="9 22 9 12 15 12 15 22"/>
                </svg>
            </span>
            <span class="nav-label">Trang Khách Hàng</span>
        </a>

        <a class="admin-nav-item" href="${ctx}/MainController?action=profile">
            <span class="nav-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                    <circle cx="12" cy="7" r="4"/>
                </svg>
            </span>
            <span class="nav-label">Hồ Sơ Admin</span>
        </a>
    </nav>

    <!-- Bottom Admin Profile Identity -->
    <div class="sidebar-profile">
        <div class="adm-avatar">
            <c:set var="admParts" value="${fn:split(sessionScope.account.fullName, ' ')}"/>
            <c:choose>
                <c:when test="${fn:length(admParts) > 1}">
                    ${fn:toUpperCase(fn:substring(admParts[0], 0, 1))}${fn:toUpperCase(fn:substring(admParts[fn:length(admParts) - 1], 0, 1))}
                </c:when>
                <c:otherwise>AD</c:otherwise>
            </c:choose>
        </div>
        <div class="adm-info">
            <div class="name"><c:out value="${sessionScope.account.fullName}"/></div>
            <div class="role">Quản Trị Viên</div>
        </div>
        <a class="adm-logout-btn" href="${ctx}/MainController?action=logout" title="Đăng xuất">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                <polyline points="16 17 21 12 16 7"/>
                <line x1="21" y1="12" x2="9" y2="12"/>
            </svg>
        </a>
    </div>
</aside>
