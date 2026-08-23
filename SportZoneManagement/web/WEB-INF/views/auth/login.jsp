<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Đăng nhập — SportZone" scope="request"/>
<c:set var="pageCss" value="auth-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-auth" scope="request"/>
<c:set var="headerVariant" value="none" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<a class="top-home-link" href="${ctx}/MainController?action=home"><span>← Trang chủ SportZone</span></a>
<div class="auth-container">
    <div class="auth-hero">
        <img class="auth-hero-bg" src="https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?auto=format&amp;fit=crop&amp;w=1600&amp;q=88" alt="SportZone Arena">
        <div class="auth-hero-content">
            <a class="brand-logo" href="${ctx}/MainController?action=home"><svg viewBox="0 0 48 48" aria-hidden="true"><rect x="2" y="2" width="44" height="44" rx="14" fill="#0b1911"/><path d="M12 24h24M24 12v24" stroke="#caa54b" stroke-width="1.6"/><path d="M16 14h16a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H16a2 2 0 0 1-2-2V16a2 2 0 0 1 2-2Z" fill="none" stroke="#fff" stroke-width="1.5"/><circle cx="24" cy="24" r="3.5" fill="#1b5e3b" stroke="#fff" stroke-width="1.2"/></svg><span>Sport<span class="zone">Zone</span></span></a>
            <h1 class="hero-heading">Khung Giờ Vàng.<br><span class="gold">Sẵn Sàng Ra Sân.</span></h1>
            <p class="hero-desc">Hệ thống tổ hợp 10 sân Tennis &amp; Pickleball đạt chuẩn quốc tế tại TP. Hồ Chí Minh. Đặt sân tức thì trong 60s và tận hưởng trọn vẹn từng trận đấu.</p>
            <div class="auth-features"><div class="auth-feat-item"><i>✓</i><span>100% Giữ chỗ theo thời gian thực — không lo trùng lịch</span></div><div class="auth-feat-item"><i>✓</i><span>Xem lịch 7 ngày liên tiếp &amp; chọn slot giờ linh hoạt</span></div><div class="auth-feat-item"><i>✓</i><span>Dịch vụ trọn gói thuê vợt, bóng nỉ &amp; nước uống tại quầy</span></div></div>
        </div>
    </div>
    <div class="auth-form-panel">
        <div class="auth-tabs"><a class="auth-tab-btn active" href="${ctx}/MainController?action=login">Đăng nhập</a><a class="auth-tab-btn" href="${ctx}/MainController?action=register">Đăng ký tài khoản</a></div>
        <div class="auth-form-wrapper active">
            <div class="form-title font-heading">Chào Mừng Trở Lại!</div>
            <p class="form-sub">Nhập email và mật khẩu của bạn để truy cập hệ thống đặt sân.</p>
            <c:if test="${not empty errorMessage}"><div class="sz-alert sz-alert-danger"><span>⚠️ <c:out value="${errorMessage}"/></span></div></c:if>
            <form id="loginForm" method="post" action="${ctx}/MainController?action=login" novalidate>
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <div class="form-group"><label class="form-label" for="loginEmail">Email đăng nhập</label><div class="input-wrapper"><input class="form-input" type="email" id="loginEmail" name="email" value="<c:out value='${email}'/>" placeholder="name@example.com" required autofocus></div></div>
                <div class="form-group"><label class="form-label" for="loginPassword">Mật khẩu</label><div class="input-wrapper"><input class="form-input" type="password" id="loginPassword" name="password" placeholder="Nhập mật khẩu" required><button type="button" class="toggle-pass-btn" onclick="togglePassVisibility('loginPassword',this)" aria-label="Hiện mật khẩu">👁</button></div></div>
                <button class="btn-submit" type="submit"><span>Đăng nhập hệ thống →</span></button>
            </form>
            <div class="form-switch-prompt">Chưa có tài khoản SportZone? <a href="${ctx}/MainController?action=register">Đăng ký ngay</a></div>
        </div>
    </div>
</div>
<script>function togglePassVisibility(id,button){var input=document.getElementById(id);var show=input.type==='password';input.type=show?'text':'password';button.textContent=show?'🙈':'👁';}</script>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
