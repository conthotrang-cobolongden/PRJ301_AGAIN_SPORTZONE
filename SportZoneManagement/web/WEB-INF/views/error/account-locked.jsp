<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Tài khoản đã bị khóa — SportZone" scope="request"/>
<c:set var="pageCss" value="error-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-error" scope="request"/>
<c:set var="headerVariant" value="none" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="container error-page-shell">
    <div class="error-card error-view active">
        <div class="error-code code-403">403</div>
        <div class="error-title">Tài Khoản Đã Bị Khóa</div>
        <p class="error-desc">Xin chào <b><c:out value="${account.fullName}"/></b>. Tài khoản của bạn hiện đã bị vô hiệu hóa. Vui lòng liên hệ SportZone để được kiểm tra và hỗ trợ mở khóa.</p>
        <div class="locked-contact"><span>Hotline: <b>(+84) 901 234 567</b></span><span>Email: <b>support@sportzone.vn</b></span></div>
        <a class="btn-home" href="${ctx}/MainController?action=logout">Đăng Xuất</a>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
