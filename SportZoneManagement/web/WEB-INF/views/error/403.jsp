<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="403 — Không có quyền" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container sz-section sz-error">
    <p class="sz-kicker">403</p>
    <h1>Bạn không có quyền xem trang này.</h1>
    <p>Đăng nhập đúng tài khoản hoặc quay về trang chủ.</p>
    <a class="btn btn-sz" href="${ctx}/MainController?action=home">Về trang chủ</a>
</section>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
