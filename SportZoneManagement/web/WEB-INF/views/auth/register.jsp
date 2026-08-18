<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Đăng ký — SportZone" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container sz-section">
    <div class="row justify-content-center">
        <div class="col-md-7 col-lg-6">
            <div class="sz-card">
                <p class="sz-kicker">SportZone</p>
                <h2>Đăng ký tài khoản</h2>

                <form id="registerForm" method="post" action="${ctx}/register" novalidate>
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>

                    <div class="mb-3">
                        <label class="form-label" for="fullName">Họ tên</label>
                        <input class="form-control <c:if test='${not empty fieldErrors.fullName}'>is-invalid</c:if>"
                               type="text" id="fullName" name="fullName" maxlength="100"
                               value="<c:out value='${fullName}'/>" required autofocus>
                        <div class="invalid-feedback"><c:out value="${fieldErrors.fullName}"/></div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="email">Email</label>
                        <input class="form-control <c:if test='${not empty fieldErrors.email}'>is-invalid</c:if>"
                               type="email" id="email" name="email" maxlength="100"
                               value="<c:out value='${email}'/>" required>
                        <div class="invalid-feedback"><c:out value="${fieldErrors.email}"/></div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="phone">Số điện thoại</label>
                        <input class="form-control <c:if test='${not empty fieldErrors.phone}'>is-invalid</c:if>"
                               type="tel" id="phone" name="phone" maxlength="20"
                               value="<c:out value='${phone}'/>" required
                               placeholder="VD: 0901234567">
                        <div class="invalid-feedback"><c:out value="${fieldErrors.phone}"/></div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="password">Mật khẩu</label>
                        <input class="form-control <c:if test='${not empty fieldErrors.password}'>is-invalid</c:if>"
                               type="password" id="password" name="password"
                               minlength="8" maxlength="64" required>
                        <div class="invalid-feedback"><c:out value="${fieldErrors.password}"/></div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="confirmPassword">Nhập lại mật khẩu</label>
                        <input class="form-control <c:if test='${not empty fieldErrors.confirmPassword}'>is-invalid</c:if>"
                               type="password" id="confirmPassword" name="confirmPassword"
                               minlength="8" maxlength="64" required>
                        <div class="invalid-feedback"><c:out value="${fieldErrors.confirmPassword}"/></div>
                    </div>

                    <button class="btn btn-sz w-100" type="submit">Đăng ký</button>
                </form>

                <p class="mt-3 mb-0">Đã có tài khoản?
                    <a href="${ctx}/login">Đăng nhập</a>
                </p>
            </div>
        </div>
    </div>
</section>

<script>
    // Validate phía client theo U1 — chỉ bổ trợ UX; server vẫn là chốt cuối.
    (function () {
        var form = document.getElementById('registerForm');
        var EMAIL_RE = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;
        var PHONE_RE = /^(0|\+84)(3|5|7|8|9)\d{8}$/;

        function setError(input, message) {
            var feedback = input.parentElement.querySelector('.invalid-feedback');
            if (message) {
                input.classList.add('is-invalid');
                if (feedback) feedback.textContent = message;
            } else {
                input.classList.remove('is-invalid');
            }
        }

        form.addEventListener('submit', function (event) {
            var fullName = document.getElementById('fullName');
            var email = document.getElementById('email');
            var phone = document.getElementById('phone');
            var password = document.getElementById('password');
            var confirm = document.getElementById('confirmPassword');
            var ok = true;

            if (!fullName.value.trim()) { setError(fullName, 'Họ tên không được để trống'); ok = false; }
            else setError(fullName, '');

            if (!EMAIL_RE.test(email.value.trim())) { setError(email, 'Email không đúng định dạng'); ok = false; }
            else setError(email, '');

            var compactPhone = phone.value.replace(/[\s-]/g, '');
            if (!PHONE_RE.test(compactPhone)) { setError(phone, 'Số điện thoại không đúng định dạng Việt Nam'); ok = false; }
            else setError(phone, '');

            if (password.value.length < 8 || password.value.length > 64) {
                setError(password, 'Mật khẩu phải từ 8 đến 64 ký tự'); ok = false;
            } else setError(password, '');

            if (confirm.value !== password.value) { setError(confirm, 'Nhập lại mật khẩu không khớp'); ok = false; }
            else setError(confirm, '');

            if (!ok) event.preventDefault();
        });
    })();
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
