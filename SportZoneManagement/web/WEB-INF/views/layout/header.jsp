<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${empty pageTitle ? 'SportZone — Thể Thao Đỉnh Cao' : pageTitle}"/></title>
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800;850&display=swap" rel="stylesheet">
    <!-- Bootstrap 5 & Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- SportZone Custom App CSS -->
    <link href="${ctx}/assets/css/app.css" rel="stylesheet">
</head>
<body>
<header class="sz-header">
    <nav class="navbar navbar-expand-lg">
        <div class="container">
            <a class="sz-brand-logo" href="${ctx}/home">
                <span class="sz-mark">SZ</span>
                <span class="sz-name">Sport<span style="color:var(--sz-green)">Zone</span></span>
            </a>
            <button class="navbar-toggler border-0 shadow-none" type="button" data-bs-toggle="collapse" data-bs-target="#szNav">
                <i class="bi bi-list fs-2" style="color:var(--sz-ink)"></i>
            </button>
            <div class="collapse navbar-collapse" id="szNav">
                <ul class="navbar-nav ms-auto align-items-lg-center gap-lg-2">
                    <li class="nav-item"><a class="nav-link" href="${ctx}/home"><i class="bi bi-house-door me-1"></i>Trang chủ</a></li>
                    <li class="nav-item"><a class="nav-link" href="${ctx}/courts"><i class="bi bi-grid-3x3-gap me-1"></i>Danh sách sân</a></li>
                    <c:choose>
                        <c:when test="${not empty sessionScope.account}">
                            <c:if test="${sessionScope.roleName != 'Admin'}">
                                <li class="nav-item"><a class="nav-link" href="${ctx}/bookings"><i class="bi bi-calendar-check me-1"></i>Đặt sân</a></li>
                            </c:if>
                            <li class="nav-item">
                                <a class="nav-link fw-bold" href="${ctx}/profile" style="color:var(--sz-green)">
                                    <i class="bi bi-person-circle me-1"></i>${sessionScope.account.fullName}
                                </a>
                            </li>
                            <li class="nav-item ms-lg-2">
                                <a class="btn btn-sz-outline btn-sz-sm" href="${ctx}/logout">
                                    <i class="bi bi-box-arrow-right"></i> Đăng xuất
                                </a>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="nav-item ms-lg-2">
                                <a class="nav-link" href="${ctx}/login"><i class="bi bi-box-arrow-in-right me-1"></i>Đăng nhập</a>
                            </li>
                            <li class="nav-item">
                                <a class="btn btn-sz btn-sz-sm" href="${ctx}/register">
                                    <i class="bi bi-person-plus-fill"></i> Đăng ký
                                </a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </nav>
</header>

<c:if test="${sessionScope.frozen}">
    <div class="alert alert-warning sz-frozen-banner mb-0 rounded-0 text-center border-0 shadow-sm" style="background:#fef3c7; color:#92400e; font-weight:600">
        <i class="bi bi-exclamation-triangle-fill me-2"></i>Tài khoản đang bị khóa — chỉ xem được hồ sơ và hóa đơn cũ, không thể đặt sân.
    </div>
</c:if>

<main class="sz-main">
