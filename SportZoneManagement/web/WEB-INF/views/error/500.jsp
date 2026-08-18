<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="500 — Lỗi hệ thống" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container sz-section sz-error">
    <p class="sz-kicker">500</p>
    <h1>Có lỗi xảy ra.</h1>
    <p>Thử lại sau hoặc quay về trang chủ.</p>
    <a class="btn btn-sz" href="${ctx}/home">Về trang chủ</a>
</section>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
