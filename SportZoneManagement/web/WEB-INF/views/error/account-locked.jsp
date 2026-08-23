<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx"
       value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Tài khoản đã bị khóa — SportZone</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
          rel="stylesheet">

    <link href="${ctx}/assets/css/app.css"
          rel="stylesheet">
</head>

<body class="d-flex align-items-center justify-content-center min-vh-100">
    <main class="container py-5">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                <div class="card border-0 shadow-lg rounded-4">
                    <div class="card-body p-4 p-md-5 text-center">
                        <div class="display-4 text-danger mb-3">
                            <i class="bi bi-person-lock"></i>
                        </div>

                        <p class="text-uppercase fw-bold text-danger small mb-2">
                            Truy cập bị hạn chế
                        </p>

                        <h1 class="h3 mb-3">
                            Tài khoản đã bị khóa
                        </h1>

                        <p class="text-muted mb-2">
                            Xin chào
                            <strong>
                                <c:out value="${account.fullName}"/>
                            </strong>.
                        </p>

                        <p class="text-muted mb-4">
                            Tài khoản của bạn hiện đã bị vô hiệu hóa.
                            Vui lòng liên hệ SportZone để được kiểm tra
                            và hỗ trợ mở khóa.
                        </p>

                        <div class="alert alert-warning text-start rounded-3">
                            <div class="mb-2">
                                <i class="bi bi-telephone-fill me-2"></i>
                                Hotline:
                                <strong>(+84) 901 234 567</strong>
                            </div>

                            <div>
                                <i class="bi bi-envelope-fill me-2"></i>
                                Email:
                                <strong>support@sportzone.vn</strong>
                            </div>
                        </div>

                        <a class="btn btn-danger px-4"
                           href="${ctx}/MainController?action=logout">
                            <i class="bi bi-box-arrow-right me-1"></i>
                            Đăng xuất
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </main>
</body>
</html>
