<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${empty pageTitle ? 'SportZone — Thể Thao Đỉnh Cao' : pageTitle}"/></title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&amp;family=Montserrat:wght@600;700;800;900&amp;display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${ctx}/assets/css/app.css" rel="stylesheet">
    <c:choose>
        <c:when test="${not empty pageCss}"><link href="${ctx}/assets/css/design/${pageCss}" rel="stylesheet"></c:when>
        <c:otherwise><link href="${ctx}/assets/css/design/home-design.css" rel="stylesheet"></c:otherwise>
    </c:choose>
</head>
<body class="${pageBodyClass}">
<c:choose>
    <c:when test="${headerVariant == 'none'}"/>
    <c:when test="${headerVariant == 'standard'}">
        <header class="sz-nav"><div class="container nav-inner">
            <a class="brand-logo" href="${ctx}/MainController?action=home" aria-label="SportZone Home">
                <svg viewBox="0 0 48 48" aria-hidden="true"><rect x="2" y="2" width="44" height="44" rx="14" fill="#1b5e3b"/><path d="M12 24h24M24 12v24" stroke="#caa54b" stroke-width="1.6"/><path d="M16 14h16a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H16a2 2 0 0 1-2-2V16a2 2 0 0 1 2-2Z" fill="none" stroke="#fff" stroke-width="1.5"/><circle cx="24" cy="24" r="3.5" fill="#caa54b"/></svg>
                <span>Sport<span class="zone">Zone</span></span>
            </a>
            <nav class="nav-links" aria-label="Điều hướng chính">
                <a class="nav-link" href="${ctx}/MainController?action=home">Trang Chủ</a>
                <a class="nav-link" href="${ctx}/MainController?action=courts">Danh Sách Sân</a>
                <c:choose><c:when test="${not empty sessionScope.account}"><c:choose><c:when test="${sessionScope.roleName == 'Admin'}"><a class="nav-link" href="${ctx}/MainController?action=adminDashboard" style="color:var(--gold);font-weight:800">Admin Suite</a><a class="nav-link" href="${ctx}/MainController?action=adminBookings">Bookings</a><a class="nav-link" href="${ctx}/MainController?action=adminCourts">Courts</a></c:when><c:otherwise><a class="nav-link" href="${ctx}/MainController?action=bookings">Đặt Sân</a><a class="nav-link" href="${ctx}/MainController?action=invoices">Hóa Đơn</a></c:otherwise></c:choose><a class="nav-link" href="${ctx}/MainController?action=profile"><c:out value="${sessionScope.account.fullName}"/></a><a class="nav-link" href="${ctx}/MainController?action=logout">Đăng Xuất</a></c:when><c:otherwise><a class="nav-link" href="${ctx}/MainController?action=login">Đăng Nhập</a><a class="nav-link" href="${ctx}/MainController?action=register">Đăng Ký</a></c:otherwise></c:choose>
            </nav>
        </div></header>
    </c:when>
    <c:otherwise>
        <header class="header" id="header"><div class="shell nav">
            <a class="brand" href="${ctx}/MainController?action=home" aria-label="SportZone Home"><svg viewBox="0 0 48 48" aria-hidden="true"><rect x="2" y="2" width="44" height="44" rx="14" fill="#0b1911"/><path d="M12 24h24M24 12v24" stroke="#caa54b" stroke-width="1.6" opacity=".9"/><path d="M16 14h16a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H16a2 2 0 0 1-2-2V16a2 2 0 0 1 2-2Z" fill="none" stroke="#fff" stroke-width="1.5" opacity=".9"/><circle cx="24" cy="24" r="3.5" fill="#1b5e3b" stroke="#fff" stroke-width="1.2"/></svg><span>Sport<span class="zone">Zone</span></span></a>
            <nav class="menu" aria-label="Điều hướng chính"><a href="${ctx}/MainController?action=home">Trang chủ</a><a href="${ctx}/MainController?action=courts">Danh sách sân</a><c:if test="${not empty sessionScope.account && sessionScope.roleName != 'Admin'}"><a href="${ctx}/MainController?action=bookings">Booking của tôi</a></c:if><c:if test="${sessionScope.roleName == 'Admin'}"><a href="${ctx}/MainController?action=adminDashboard">Admin Suite</a><a href="${ctx}/MainController?action=adminBookings">Bookings</a><a href="${ctx}/MainController?action=adminCourts">Courts</a></c:if></nav>
            <div class="nav-actions"><c:choose><c:when test="${not empty sessionScope.account}"><a class="btn login" href="${ctx}/MainController?action=profile"><c:out value="${sessionScope.account.fullName}"/></a><a class="btn primary" href="${ctx}/MainController?action=logout">Đăng xuất <span>→</span></a></c:when><c:otherwise><a class="btn login" href="${ctx}/MainController?action=login">Đăng nhập</a><a class="btn primary" href="${ctx}/MainController?action=courts">Xem sân trống <span>→</span></a></c:otherwise></c:choose><button class="hamb" id="hamb" type="button" aria-label="Mở menu" aria-expanded="false">☰</button></div>
        </div></header>
        <div class="mobile-menu" id="mobileMenu" aria-hidden="true"><a href="${ctx}/MainController?action=home">Trang chủ</a><a href="${ctx}/MainController?action=courts">Danh sách sân</a><c:choose><c:when test="${not empty sessionScope.account}"><a href="${ctx}/MainController?action=profile">Tài khoản</a><a href="${ctx}/MainController?action=logout" class="small-link">Đăng xuất</a></c:when><c:otherwise><a href="${ctx}/MainController?action=login" class="small-link">Đăng nhập / Đăng ký</a></c:otherwise></c:choose></div>
    </c:otherwise>
</c:choose>
<c:if test="${sessionScope.frozen}"><div class="alert alert-warning sz-frozen-banner mb-0 rounded-0 text-center border-0" role="alert"><i class="bi bi-exclamation-triangle-fill me-2"></i>Tài khoản đang bị khóa — vui lòng đăng xuất hoặc liên hệ hỗ trợ.</div></c:if>
<main class="sz-main">
