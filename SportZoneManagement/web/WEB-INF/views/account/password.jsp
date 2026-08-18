<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- U6: Đổi mật khẩu (hash SHA-256, CSRF bắt buộc) --%>
<c:set var="pageTitle" value="Đổi mật khẩu — SportZone" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container sz-section">
    <div class="sz-form-narrow">
        <div class="sz-pagehead">
            <div>
                <p class="section-label mb-1">Tài khoản</p>
                <h1>Đổi mật khẩu</h1>
            </div>
        </div>

        <!-- DEEPSEEK: success (nếu redirect về trang này), error chung,
             fieldErrors = Map (currentPassword/newPassword/confirmPassword) -->
        <c:if test="${not empty success}">
            <div class="alert alert-success" role="alert"><c:out value="${success}"/></div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
        </c:if>

        <div class="sz-card">
            <form method="post" action="${ctx}/password" id="szPasswordForm" novalidate>
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>

                <div class="mb-3">
                    <label class="form-label" for="currentPassword">Mật khẩu hiện tại</label>
                    <input class="form-control ${not empty fieldErrors.currentPassword ? 'is-invalid' : ''}"
                           type="password" id="currentPassword" name="currentPassword"
                           autocomplete="current-password" required>
                    <div class="invalid-feedback"><c:out value="${fieldErrors.currentPassword}"/></div>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="newPassword">Mật khẩu mới</label>
                    <input class="form-control ${not empty fieldErrors.newPassword ? 'is-invalid' : ''}"
                           type="password" id="newPassword" name="newPassword"
                           autocomplete="new-password" minlength="8" required>
                    <div class="invalid-feedback"><c:out value="${fieldErrors.newPassword}"/></div>
                    <div class="form-text">Ít nhất 8 ký tự, nên gồm chữ hoa, chữ thường và số.</div>
                </div>

                <div class="mb-4">
                    <label class="form-label" for="confirmPassword">Nhập lại mật khẩu mới</label>
                    <input class="form-control ${not empty fieldErrors.confirmPassword ? 'is-invalid' : ''}"
                           type="password" id="confirmPassword" name="confirmPassword"
                           autocomplete="new-password" minlength="8" required>
                    <div class="invalid-feedback"><c:out value="${fieldErrors.confirmPassword}"/></div>
                </div>

                <button class="btn btn-sz w-100" type="submit">Đổi mật khẩu</button>
            </form>
            <p class="mt-3 mb-0"><a href="${ctx}/profile">&larr; Quay lại hồ sơ</a></p>
        </div>
    </div>
</section>

<script>
// Validate client tối thiểu (server validate lại — U6)
(function () {
    var form = document.getElementById('szPasswordForm');
    if (!form) return;
    form.addEventListener('submit', function (e) {
        var np = document.getElementById('newPassword');
        var cf = document.getElementById('confirmPassword');
        var ok = true;
        cf.classList.remove('is-invalid');
        if (np.value.length < 8) { np.classList.add('is-invalid'); ok = false; }
        if (cf.value !== np.value) {
            cf.classList.add('is-invalid');
            cf.nextElementSibling.textContent = 'Mật khẩu nhập lại không khớp.';
            ok = false;
        }
        if (!ok) e.preventDefault();
    });
})();
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
