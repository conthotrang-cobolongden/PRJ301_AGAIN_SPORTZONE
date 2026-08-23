<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="500 — Lỗi hệ thống" scope="request"/>
<c:set var="pageCss" value="error-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-error" scope="request"/>
<c:set var="headerVariant" value="none" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="container error-page-shell">
    <div class="error-card error-view active">
        <div class="error-code code-500">500</div>
        <div class="error-title">Hệ Thống Đang Xử Lý</div>
        <p class="error-desc">Đã có lỗi phát sinh trong quá trình kết nối hệ thống. Đội ngũ kỹ thuật SportZone đã ghi nhận và đang khắc phục.</p>
        <a class="btn-home" href="${ctx}/MainController?action=home">Tải Lại Trang Chủ</a>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
