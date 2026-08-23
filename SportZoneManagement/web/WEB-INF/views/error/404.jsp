<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="404 — Không tìm thấy" scope="request"/>
<c:set var="pageCss" value="error-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-error" scope="request"/>
<c:set var="headerVariant" value="none" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="container error-page-shell">
    <div class="error-card error-view active">
        <div class="error-code code-404">404</div>
        <div class="error-title">Không Tìm Thấy Trang</div>
        <p class="error-desc">Địa chỉ trang hoặc mã sân thể thao bạn đang tìm kiếm không tồn tại hoặc đã được chuyển sang danh mục khác.</p>
        <a class="btn-home" href="${ctx}/MainController?action=courts">Xem Danh Sách Sân</a>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
