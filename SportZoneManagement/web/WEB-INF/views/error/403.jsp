<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="403 — Không có quyền" scope="request"/>
<c:set var="pageCss" value="error-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-error" scope="request"/>
<c:set var="headerVariant" value="none" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="container error-page-shell">
    <div class="error-card error-view active">
        <div class="error-code code-403">403</div>
        <div class="error-title">Truy Cập Bị Từ Chối</div>
        <p class="error-desc">Bạn không có quyền truy cập vào phân vùng này, hoặc tài khoản đang ở trạng thái <b>Tạm Khóa</b>. Vui lòng liên hệ lễ tân để được hỗ trợ.</p>
        <a class="btn-home" href="${ctx}/MainController?action=home">Về Trang Chủ SportZone</a>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
