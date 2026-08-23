<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="404 — Không tìm thấy" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container sz-section sz-error">
    <p class="sz-kicker">404</p>
    <h1>Trang chưa có hoặc đường dẫn sai.</h1>
    <p>Một số mục (đăng nhập, danh sách sân) sẽ mở ở phase sau.</p>
    <a class="btn btn-sz" href="${ctx}/MainController?action=home">Về trang chủ</a>
</section>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
