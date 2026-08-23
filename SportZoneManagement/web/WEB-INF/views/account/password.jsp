<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Đổi Mật Khẩu — SportZone" scope="request"/>
<c:set var="pageCss" value="account-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-account" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- ==============================================
     CHANGE PASSWORD (U6) — SPORTZONE TEXT-ONLY LUXURY
     ============================================== -->
<div class="shell" style="padding-top:40px; padding-bottom:60px;">
    <!-- Page Header -->
    <div class="page-header">
        <div class="page-title">
            <div class="kicker"><span class="dot"></span> TÀI KHOẢN THÀNH VIÊN SPORTZONE</div>
            <h1>Hồ Sơ &amp; <span class="accent-text">Cài Đặt Cá Nhân</span></h1>
            <p>Quản lý thông tin liên hệ, theo dõi đặc quyền thành viên và bảo mật tài khoản đặt sân.</p>
        </div>
    </div>

    <!-- Alert Notifications -->
    <c:if test="${not empty success}">
        <div class="sz-alert sz-alert-success" style="border-radius:14px; margin-bottom:24px;">
            <c:out value="${success}"/>
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="sz-alert sz-alert-danger" style="border-radius:14px; margin-bottom:24px;">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <div class="account-layout">
        <!-- ==============================================
             LEFT COLUMN: PROFILE IDENTIFIER CARD (U4)
             ============================================== -->
        <aside class="profile-card">
            <c:set var="nameParts" value="${fn:split(sessionScope.account.fullName, ' ')}"/>
            <div class="avatar-wrapper">
                <div class="avatar-main" aria-hidden="true">
                    <c:choose>
                        <c:when test="${fn:length(nameParts) > 1}">
                            ${fn:toUpperCase(fn:substring(nameParts[0], 0, 1))}${fn:toUpperCase(fn:substring(nameParts[fn:length(nameParts) - 1], 0, 1))}
                        </c:when>
                        <c:otherwise>
                            ${fn:toUpperCase(fn:substring(sessionScope.account.fullName, 0, 2))}
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="avatar-badge-vip">VIP</div>
            </div>
            
            <h2 class="user-name"><c:out value="${sessionScope.account.fullName}"/></h2>
            
            <div class="user-meta">
                <span class="sz-badge sz-badge-dark">${sessionScope.account.roleName == 'Admin' ? 'Quản trị viên' : 'Khách hàng'}</span>
                <c:choose>
                    <c:when test="${sessionScope.account.status == 'Active'}">
                        <span class="sz-badge sz-badge-active">Hoạt động</span>
                    </c:when>
                    <c:otherwise>
                        <span class="sz-badge sz-badge-frozen">Khóa</span>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="profile-kv-list">
                <div class="profile-kv-item">
                    <span class="lbl">Email</span>
                    <span class="val"><c:out value="${sessionScope.account.email}"/></span>
                </div>
                <div class="profile-kv-item">
                    <span class="lbl">Số điện thoại</span>
                    <span class="val"><c:out value="${sessionScope.account.phone}"/></span>
                </div>
            </div>

            <div class="side-nav">
                <a class="side-nav-btn" href="${ctx}/MainController?action=profile">Thông tin cá nhân</a>
                <a class="side-nav-btn active" href="${ctx}/MainController?action=password">Đổi mật khẩu</a>
            </div>
        </aside>

        <!-- ==============================================
             RIGHT COLUMN: CHANGE PASSWORD FORM (U6)
             ============================================== -->
        <section class="content-card">
            <!-- Navigation Tabs -->
            <div class="content-tabs">
                <a class="content-tab-btn" href="${ctx}/MainController?action=profile">Thông Tin Cá Nhân</a>
                <a class="content-tab-btn active" href="${ctx}/MainController?action=password">Đổi Mật Khẩu</a>
            </div>

            <div class="tab-section active">
                <div class="font-heading" style="font-size:22px; font-weight:800; margin-bottom:6px; color:var(--ink);">
                    Đổi Mật Khẩu Bảo Mật
                </div>
                <p style="font-size:13.5px; color:var(--muted); margin-bottom:28px; line-height:1.6;">
                    Để bảo vệ an toàn cho tài khoản đặt sân, mật khẩu mới phải từ 8 đến 64 ký tự.
                </p>

                <form method="post" action="${ctx}/MainController?action=password" id="szPasswordForm" novalidate>
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>

                    <div class="form-group mb-4">
                        <label class="form-label" for="currentPassword">Mật khẩu hiện tại</label>
                        <div class="input-pass-wrap">
                            <input class="form-input ${not empty fieldErrors.currentPassword ? 'is-invalid' : ''}"
                                   type="password" id="currentPassword" name="currentPassword" maxlength="64"
                                   autocomplete="current-password" required>
                            <button type="button" class="toggle-pass-btn" onclick="togglePass('currentPassword',this)">Hiện</button>
                        </div>
                        <div class="invalid-feedback"><c:out value="${fieldErrors.currentPassword}"/></div>
                    </div>

                    <div class="form-grid mb-4">
                        <div class="form-group">
                            <label class="form-label" for="newPassword">Mật khẩu mới</label>
                            <div class="input-pass-wrap">
                                <input class="form-input ${not empty fieldErrors.newPassword ? 'is-invalid' : ''}"
                                       type="password" id="newPassword" name="newPassword" minlength="8" maxlength="64"
                                       autocomplete="new-password" required>
                                <button type="button" class="toggle-pass-btn" onclick="togglePass('newPassword',this)">Hiện</button>
                            </div>
                            <div class="invalid-feedback"><c:out value="${fieldErrors.newPassword}"/></div>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="confirmPassword">Xác nhận mật khẩu mới</label>
                            <div class="input-pass-wrap">
                                <input class="form-input ${not empty fieldErrors.confirmPassword ? 'is-invalid' : ''}"
                                       type="password" id="confirmPassword" name="confirmPassword" minlength="8" maxlength="64"
                                       autocomplete="new-password" required>
                                <button type="button" class="toggle-pass-btn" onclick="togglePass('confirmPassword',this)">Hiện</button>
                            </div>
                            <div class="invalid-feedback"><c:out value="${fieldErrors.confirmPassword}"/></div>
                        </div>
                    </div>

                    <button class="btn-gold" type="submit" style="font-weight:800; padding:14px 36px; border-radius:999px;">
                        Cập Nhật Mật Khẩu Mới
                    </button>
                </form>
            </div>
        </section>
    </div>
</div>

<script>
    function togglePass(id, button) {
        var input = document.getElementById(id);
        var show = input.type === 'password';
        input.type = show ? 'text' : 'password';
        button.textContent = show ? 'Ẩn' : 'Hiện';
    }
    (function () {
        var form = document.getElementById('szPasswordForm');
        if (!form) return;
        form.addEventListener('submit', function (e) {
            var next = document.getElementById('newPassword');
            var confirm = document.getElementById('confirmPassword');
            if (next.value.length < 8 || next.value !== confirm.value) {
                e.preventDefault();
                confirm.classList.add('is-invalid');
            }
        });
    })();
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
