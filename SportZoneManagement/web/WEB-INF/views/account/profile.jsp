<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- U4: Xem hồ sơ + U5: Cập nhật name/phone/email — SportZone Luxury Design System --%>
<c:set var="pageTitle" value="Hồ Sơ Cá Nhân — SportZone" scope="request"/>
<c:set var="pageCss" value="account-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-account" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- ==============================================
     USER PROFILE (U4 + U5) — SPORTZONE TEXT-ONLY LUXURY
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

    <!-- Frozen Account Notice -->
    <c:if test="${sessionScope.frozen}">
        <div class="frozen-banner" style="margin-bottom:28px;">
            <strong>Thông báo tài khoản:</strong> Tài khoản đang bị khóa — chỉ có thể xem hồ sơ và lịch sử hóa đơn cũ, không thể tạo đơn đặt sân mới.
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
            
            <c:if test="${not empty memberSince}">
                <p class="user-joined">
                    Thành viên từ <strong><c:out value="${memberSince}"/></strong>
                </p>
            </c:if>

            <div class="profile-kv-list">
                <div class="profile-kv-item">
                    <span class="lbl">Email</span>
                    <span class="val"><c:out value="${sessionScope.account.email}"/></span>
                </div>
                <div class="profile-kv-item">
                    <span class="lbl">Số điện thoại</span>
                    <span class="val"><c:out value="${sessionScope.account.phone}"/></span>
                </div>
                <div class="profile-kv-item">
                    <span class="lbl">Hạng thành viên</span>
                    <span class="val" style="color:var(--gold); font-weight:800;">
                        <c:out value="${not empty membershipSummary.tier ? membershipSummary.tier : 'Gold Member'}"/>
                    </span>
                </div>
                <c:if test="${sessionScope.account.roleName == 'User' && not empty membershipSummary.currentMonthSpent}">
                    <div class="profile-kv-item">
                        <span class="lbl">Chi tiêu tháng</span>
                        <span class="val" style="color:var(--green); font-weight:800;">
                            <fmt:formatNumber value="${membershipSummary.currentMonthSpent}" pattern="#,##0"/> đ
                        </span>
                    </div>
                </c:if>
            </div>

            <div class="side-nav">
                <a class="side-nav-btn active" href="${ctx}/MainController?action=profile">Thông tin cá nhân</a>
                <a class="side-nav-btn" href="${ctx}/MainController?action=password">Đổi mật khẩu</a>
            </div>
        </aside>

        <!-- ==============================================
             RIGHT COLUMN: UPDATE FORM (U5)
             ============================================== -->
        <section class="content-card">
            <!-- Navigation Tabs -->
            <div class="content-tabs">
                <a class="content-tab-btn active" href="${ctx}/MainController?action=profile">Thông Tin Cá Nhân</a>
                <a class="content-tab-btn" href="${ctx}/MainController?action=password">Đổi Mật Khẩu</a>
            </div>

            <div class="tab-section active">
                <div class="font-heading" style="font-size:22px; font-weight:800; margin-bottom:6px; color:var(--ink);">
                    Cập Nhật Thông Tin
                </div>
                <p style="font-size:13.5px; color:var(--muted); margin-bottom:28px; line-height:1.6;">
                    Cập nhật tên hiển thị, số điện thoại và email liên hệ nhận thông báo đặt sân.
                </p>

                <form method="post" action="${ctx}/MainController?action=profile" novalidate>
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>

                    <div class="form-grid">
                        <div class="form-group">
                            <label class="form-label" for="fullName">Họ và tên</label>
                            <input class="form-input ${not empty fieldErrors.name ? 'is-invalid' : ''}"
                                   type="text" id="fullName" name="fullName" maxlength="100"
                                   value="<c:out value='${account.fullName}'/>" required>
                            <div class="invalid-feedback"><c:out value="${fieldErrors.name}"/></div>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="phone">Số điện thoại</label>
                            <input class="form-input ${not empty fieldErrors.phone ? 'is-invalid' : ''}"
                                   type="tel" id="phone" name="phone" maxlength="15"
                                   value="<c:out value='${account.phone}'/>" required>
                            <div class="invalid-feedback"><c:out value="${fieldErrors.phone}"/></div>
                        </div>
                    </div>

                    <div class="form-group mb-4">
                        <label class="form-label" for="email">Địa chỉ Email</label>
                        <input class="form-input ${not empty fieldErrors.email ? 'is-invalid' : ''}"
                               type="email" id="email" name="email" maxlength="150"
                               value="<c:out value='${account.email}'/>" required>
                        <div class="invalid-feedback"><c:out value="${fieldErrors.email}"/></div>
                        <div class="form-text">Email dùng để nhận thông báo lịch thi đấu và hóa đơn thanh toán.</div>
                    </div>

                    <button class="btn-green" type="submit" style="font-weight:800; padding:14px 36px; border-radius:999px;">
                        Lưu Thay Đổi Thông Tin
                    </button>
                </form>
            </div>
        </section>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
