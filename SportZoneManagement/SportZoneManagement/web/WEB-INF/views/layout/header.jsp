<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${empty pageTitle ? 'SportZone' : pageTitle}"/></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${ctx}/assets/css/app.css" rel="stylesheet">
</head>
<body>
<header class="sz-header">
    <nav class="navbar navbar-expand-lg">
        <div class="container">
            <a class="navbar-brand" href="${ctx}/home">
                <span class="sz-mark">SZ</span>
                <span class="sz-name">SportZone</span>
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#szNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="szNav">
                <ul class="navbar-nav ms-auto align-items-lg-center gap-lg-2">
                    <li class="nav-item"><a class="nav-link" href="${ctx}/home">Trang chủ</a></li>
                    <li class="nav-item"><a class="nav-link" href="${ctx}/courts">Sân</a></li>
                    <c:choose>
                        <c:when test="${not empty sessionScope.account}">
                            <%-- Link Dashboard của Admin tạm ẩn: /admin chưa tồn tại đến P8.
                                 Bỏ comment này khi P8 xong:
                            <c:if test="${sessionScope.roleName == 'Admin'}">
                                <li class="nav-item"><a class="nav-link" href="${ctx}/admin">Dashboard</a></li>
                            </c:if>
                            --%>
                            <c:if test="${sessionScope.roleName != 'Admin'}">
                                <li class="nav-item"><a class="nav-link" href="${ctx}/bookings">Đặt sân</a></li>
                            </c:if>
                            <li class="nav-item"><a class="nav-link" href="${ctx}/profile">${sessionScope.account.fullName}</a></li>
                            <li class="nav-item"><a class="btn btn-sz-outline btn-sm" href="${ctx}/logout">Đăng xuất</a></li>
                        </c:when>
                        <c:otherwise>
                            <li class="nav-item"><a class="nav-link" href="${ctx}/login">Đăng nhập</a></li>
                            <li class="nav-item"><a class="btn btn-sz btn-sm" href="${ctx}/register">Đăng ký</a></li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </nav>
</header>
<c:if test="${sessionScope.frozen}">
    <div class="alert alert-warning sz-frozen-banner mb-0 rounded-0 text-center">
        Tài khoản đang bị khóa — chỉ xem được hồ sơ và hóa đơn cũ, không thể đặt sân.
    </div>
</c:if>
<main class="sz-main">
