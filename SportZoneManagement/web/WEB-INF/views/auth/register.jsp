<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Đăng ký — SportZone" scope="request"/>
<c:set var="pageCss" value="auth-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-auth" scope="request"/>
<c:set var="headerVariant" value="none" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<a class="top-home-link" href="${ctx}/MainController?action=home"><span>← Trang chủ SportZone</span></a>
<div class="auth-container">
    <div class="auth-hero">
        <img class="auth-hero-bg" src="https://images.unsplash.com/photo-1622279457486-62dcc4a431d6?auto=format&amp;fit=crop&amp;w=1600&amp;q=88" alt="SportZone Community">
        <div class="auth-hero-content">
            <a class="brand-logo" href="${ctx}/MainController?action=home"><svg viewBox="0 0 48 48" aria-hidden="true"><rect x="2" y="2" width="44" height="44" rx="14" fill="#0b1911"/><path d="M12 24h24M24 12v24" stroke="#caa54b" stroke-width="1.6"/><path d="M16 14h16a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H16a2 2 0 0 1-2-2V16a2 2 0 0 1 2-2Z" fill="none" stroke="#fff" stroke-width="1.5"/><circle cx="24" cy="24" r="3.5" fill="#1b5e3b" stroke="#fff" stroke-width="1.2"/></svg><span>Sport<span class="zone">Zone</span></span></a>
            <h1 class="hero-heading">Gia Nhập.<br><span class="gold">Cộng Đồng Đẳng Cấp.</span></h1>
            <p class="hero-desc">Trở thành thành viên SportZone để mở khóa đặc quyền đặt trước khung giờ vàng và tận hưởng ưu đãi sân bãi.</p>
            <div class="auth-features"><div class="auth-feat-item"><i>✓</i><span>100% Giữ chỗ theo thời gian thực — không lo trùng lịch</span></div><div class="auth-feat-item"><i>✓</i><span>Xem lịch 7 ngày liên tiếp &amp; chọn slot giờ linh hoạt</span></div><div class="auth-feat-item"><i>✓</i><span>Quản lý toàn bộ booking và hóa đơn trong một tài khoản</span></div></div>
        </div>
    </div>
    <div class="auth-form-panel">
        <div class="auth-tabs"><a class="auth-tab-btn" href="${ctx}/MainController?action=login">Đăng nhập</a><a class="auth-tab-btn active" href="${ctx}/MainController?action=register">Đăng ký tài khoản</a></div>
        <div class="auth-form-wrapper active">
            <div class="form-title font-heading">Tạo Tài Khoản Mới</div>
            <p class="form-sub">Đăng ký thành viên để bắt đầu giữ chỗ và theo dõi lịch thi đấu.</p>
            <c:if test="${not empty errorMessage}"><div class="sz-alert sz-alert-danger"><span>⚠️ <c:out value="${errorMessage}"/></span></div></c:if>
            <form id="registerForm" method="post" action="${ctx}/MainController?action=register" novalidate>
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <div class="form-group"><label class="form-label" for="fullName">Họ và tên</label><div class="input-wrapper"><input class="form-input ${not empty fieldErrors.fullName ? 'is-invalid' : ''}" type="text" id="fullName" name="fullName" value="<c:out value='${fullName}'/>" placeholder="VD: Nguyễn Văn A" maxlength="100" required></div><div class="invalid-feedback"><c:out value="${fieldErrors.fullName}"/></div></div>
                <div class="form-group"><label class="form-label" for="email">Địa chỉ Email</label><div class="input-wrapper"><input class="form-input ${not empty fieldErrors.email ? 'is-invalid' : ''}" type="email" id="email" name="email" value="<c:out value='${email}'/>" placeholder="name@example.com" maxlength="100" required></div><div class="invalid-feedback"><c:out value="${fieldErrors.email}"/></div></div>
                <div class="form-group"><label class="form-label" for="phone">Số điện thoại</label><div class="input-wrapper"><input class="form-input ${not empty fieldErrors.phone ? 'is-invalid' : ''}" type="tel" id="phone" name="phone" value="<c:out value='${phone}'/>" placeholder="VD: 0901234567" maxlength="20" required></div><div class="invalid-feedback"><c:out value="${fieldErrors.phone}"/></div></div>
                <div class="form-group"><label class="form-label" for="password">Mật khẩu</label><div class="input-wrapper"><input class="form-input ${not empty fieldErrors.password ? 'is-invalid' : ''}" type="password" id="password" name="password" minlength="8" maxlength="64" placeholder="Tối thiểu 8 ký tự" required><button type="button" class="toggle-pass-btn" onclick="togglePassVisibility('password',this)" aria-label="Hiện mật khẩu">👁</button></div><div class="invalid-feedback"><c:out value="${fieldErrors.password}"/></div></div>
                <div class="form-group"><label class="form-label" for="confirmPassword">Nhập lại mật khẩu</label><div class="input-wrapper"><input class="form-input ${not empty fieldErrors.confirmPassword ? 'is-invalid' : ''}" type="password" id="confirmPassword" name="confirmPassword" minlength="8" maxlength="64" placeholder="Xác nhận lại mật khẩu" required><button type="button" class="toggle-pass-btn" onclick="togglePassVisibility('confirmPassword',this)" aria-label="Hiện mật khẩu">👁</button></div><div class="invalid-feedback"><c:out value="${fieldErrors.confirmPassword}"/></div></div>
                <button class="btn-submit" type="submit"><span>Tạo tài khoản SportZone →</span></button>
            </form>
            <div class="form-switch-prompt">Đã có tài khoản? <a href="${ctx}/MainController?action=login">Đăng nhập</a></div>
        </div>
    </div>
</div>
<script>
function togglePassVisibility(id,button){var input=document.getElementById(id);var show=input.type==='password';input.type=show?'text':'password';button.textContent=show?'🙈':'👁';}
(function(){var form=document.getElementById('registerForm');form.addEventListener('submit',function(event){var fullName=document.getElementById('fullName'),email=document.getElementById('email'),phone=document.getElementById('phone'),password=document.getElementById('password'),confirm=document.getElementById('confirmPassword');var ok=fullName.value.trim()&&/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email.value.trim())&&/^(0|\+84)(3|5|7|8|9)\d{8}$/.test(phone.value.replace(/[\s-]/g,''))&&password.value.length>=8&&password.value===confirm.value;if(!ok){event.preventDefault();}});})();
</script>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
