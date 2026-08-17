<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- U4: Xem hồ sơ + U5: Cập nhật name/phone/email — UI theo demo kit C10 --%>
<c:set var="pageTitle" value="Hồ sơ cá nhân — SportZone" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container sz-section">
    <div class="sz-pagehead">
        <div>
            <p class="section-label mb-1">Tài khoản</p>
            <h1>Hồ sơ cá nhân</h1>
        </div>
    </div>

    <!-- success/error sau POST; fieldErrors = Map (name/email/phone) -->
    <c:if test="${not empty success}">
        <div class="alert alert-success" role="alert"><c:out value="${success}"/></div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
    </c:if>

    <div class="row g-4">
        <!-- ===== Card định danh (demo kit C10) ===== -->
        <div class="col-lg-4">
            <div class="sz-card sz-profile-side">
                <c:set var="nameParts" value="${fn:split(sessionScope.account.fullName, ' ')}"/>
                <div class="sz-avatar" aria-hidden="true">${fn:toUpperCase(fn:substring(nameParts[0], 0, 1))}${fn:toUpperCase(fn:substring(nameParts[fn:length(nameParts) - 1], 0, 1))}</div>
                <h2 class="h5 mb-2"><c:out value="${sessionScope.account.fullName}"/></h2>
                <p class="mb-2">
                    <span class="sz-badge sz-badge-dark me-1">${sessionScope.account.roleName == 'Admin' ? 'Quản trị viên' : 'Khách hàng'}</span>
                    <c:choose>
                        <c:when test="${sessionScope.account.status == 'Active'}"><span class="sz-badge sz-badge-active">Hoạt động</span></c:when>
                        <c:otherwise><span class="sz-badge sz-badge-frozen">Khóa</span></c:otherwise>
                    </c:choose>
                </p>
                <p class="text-muted small mb-3">
                    Thành viên từ <fmt:formatDate value="${sessionScope.account.createdAt}" pattern="dd/MM/yyyy"/>
                </p>
                <div class="sz-kv"><dt>Email</dt><dd><c:out value="${sessionScope.account.email}"/></dd></div>
                <div class="sz-kv"><dt>Số điện thoại</dt><dd><c:out value="${sessionScope.account.phone}"/></dd></div>
                <a class="btn btn-sz-outline btn-sm mt-3" href="${ctx}/password">Đổi mật khẩu</a>
            </div>
        </div>

        <!-- ===== Form cập nhật (U5) ===== -->
        <div class="col-lg-8">
            <div class="sz-card">
                <h2 class="h5 mb-3">Thông tin cá nhân</h2>

                <c:if test="${sessionScope.frozen}">
                    <div class="alert alert-warning" role="alert">
                        Tài khoản đang bị khóa — chỉ xem được hồ sơ và hóa đơn cũ, không thể đặt sân.
                    </div>
                </c:if>

                <form method="post" action="${ctx}/profile" novalidate>
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>

                    <div class="mb-3">
                        <label class="form-label" for="fullName">Họ và tên</label>
                        <input class="form-control ${not empty fieldErrors.name ? 'is-invalid' : ''}"
                               type="text" id="fullName" name="fullName" maxlength="100"
                               value="<c:out value='${account.fullName}'/>" required>
                        <div class="invalid-feedback"><c:out value="${fieldErrors.name}"/></div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="email">Email</label>
                        <input class="form-control ${not empty fieldErrors.email ? 'is-invalid' : ''}"
                               type="email" id="email" name="email" maxlength="150"
                               value="<c:out value='${account.email}'/>" required>
                        <div class="invalid-feedback"><c:out value="${fieldErrors.email}"/></div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label" for="phone">Số điện thoại</label>
                        <input class="form-control ${not empty fieldErrors.phone ? 'is-invalid' : ''}"
                               type="tel" id="phone" name="phone" maxlength="15"
                               value="<c:out value='${account.phone}'/>" required>
                        <div class="invalid-feedback"><c:out value="${fieldErrors.phone}"/></div>
                    </div>

                    <button class="btn btn-sz" type="submit">Lưu thay đổi</button>
                </form>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
