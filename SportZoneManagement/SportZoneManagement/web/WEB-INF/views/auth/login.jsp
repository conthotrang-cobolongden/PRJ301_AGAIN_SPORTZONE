<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Đăng nhập — SportZone" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container sz-section">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <div class="sz-card">
                <p class="sz-kicker">SportZone</p>
                <h2>Đăng nhập</h2>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger"><c:out value="${error}"/></div>
                </c:if>

                <form method="post" action="${ctx}/login" novalidate>
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>

                    <div class="mb-3">
                        <label class="form-label" for="email">Email</label>
                        <input class="form-control" type="email" id="email" name="email"
                               value="<c:out value='${email}'/>" required autofocus>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="password">Mật khẩu</label>
                        <input class="form-control" type="password" id="password" name="password" required>
                    </div>

                    <button class="btn btn-sz w-100" type="submit">Đăng nhập</button>
                </form>

                <p class="mt-3 mb-0">Chưa có tài khoản?
                    <a href="${ctx}/register">Đăng ký ngay</a>
                </p>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
