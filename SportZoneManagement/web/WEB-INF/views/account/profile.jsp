<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- U4: Xem hồ sơ + U5: Cập nhật name/phone/email — UI theo demo kit C10 --%>
<c:set var="pageTitle" value="Hồ sơ cá nhân — SportZone" scope="request"/>
<c:set var="pageCss" value="account-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-account" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="shell">
    <div class="page-header">
        <div>
            <div class="kicker">Tài Khoản Thành Viên SportZone</div><h1>Hồ Sơ &amp; Cài Đặt Cá Nhân</h1><p>Quản lý thông tin liên hệ, theo dõi đặc quyền thành viên và bảo mật tài khoản đặt sân.</p>
        </div>
    </div>

    <!-- success/error sau POST; fieldErrors = Map (name/email/phone) -->
    <c:if test="${not empty success}">
        <div class="alert alert-success" role="alert"><c:out value="${success}"/></div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
    </c:if>

    <div class="account-layout">
        <!-- ===== Card định danh (demo kit C10) ===== -->
        <aside class="profile-card">
                <c:set var="nameParts" value="${fn:split(sessionScope.account.fullName, ' ')}"/>
                <div class="avatar-wrapper"><div class="avatar-main" aria-hidden="true">${fn:toUpperCase(fn:substring(nameParts[0], 0, 1))}${fn:toUpperCase(fn:substring(nameParts[fn:length(nameParts) - 1], 0, 1))}</div><div class="avatar-badge-vip">VIP</div></div>
                <h2 class="user-name"><c:out value="${sessionScope.account.fullName}"/></h2>
                <p class="user-meta">
                    <span class="sz-badge sz-badge-dark me-1">${sessionScope.account.roleName == 'Admin' ? 'Quản trị viên' : 'Khách hàng'}</span>
                    <c:choose>
                        <c:when test="${sessionScope.account.status == 'Active'}"><span class="sz-badge sz-badge-active">Hoạt động</span></c:when>
                        <c:otherwise><span class="sz-badge sz-badge-frozen">Khóa</span></c:otherwise>
                    </c:choose>
                </p>
                <p class="user-joined">
                    Thành viên từ <c:out value="${memberSince}"/>
                </p>
                <div class="profile-kv-list"><div class="profile-kv-item"><span class="lbl">Email</span><span class="val"><c:out value="${sessionScope.account.email}"/></span></div><div class="profile-kv-item"><span class="lbl">Số điện thoại</span><span class="val"><c:out value="${sessionScope.account.phone}"/></span></div><div class="profile-kv-item"><span class="lbl">Hạng thành viên</span><span class="val" style="color:var(--gold)"><c:out value="${membershipSummary.tier}"/></span></div></div>

                <c:if test="${sessionScope.account.roleName == 'User'}">
                    <hr class="my-4">

                    <div class="text-start">
                        <p class="section-label mb-2">Hạng thành viên</p>

                        <p class="mb-2">
                            <span class="sz-badge sz-badge-dark">
                                <c:out value="${membershipSummary.tier}"/>
                            </span>
                        </p>

                        <p class="small mb-2">
                            Chi tiêu tháng này:
                            <strong>
                                <fmt:formatNumber
                                    value="${membershipSummary.currentMonthSpent}"
                                    pattern="#,##0"/>
                                đ
                            </strong>
                        </p>

                        <p class="text-muted small mb-0">
                            Ưu đãi theo hạng thành viên sẽ được phát triển trong tương lai.
                        </p>
                    </div>
                </c:if>

                <div class="side-nav"><a class="side-nav-btn active" href="${ctx}/MainController?action=profile">Thông tin cá nhân</a><a class="side-nav-btn" href="${ctx}/MainController?action=password">Đổi mật khẩu</a><button class="side-nav-btn" type="button" disabled>Thống kê thi đấu · Coming Soon</button></div>
        </aside>

        <!-- ===== Form cập nhật (U5) ===== -->
        <section class="content-card">
                <div class="content-tabs"><a class="content-tab-btn active" href="${ctx}/MainController?action=profile">Thông Tin Cá Nhân</a><a class="content-tab-btn" href="${ctx}/MainController?action=password">Đổi Mật Khẩu</a><button class="content-tab-btn" type="button" disabled>Hoạt Động &amp; Thống Kê · Coming Soon</button></div>
                <div class="tab-section active"><div class="font-heading account-section-title">Cập Nhật Thông Tin</div><p class="account-section-copy">Cập nhật tên hiển thị, email và số điện thoại liên hệ nhận thông báo đặt sân.</p>

                <c:if test="${sessionScope.frozen}">
                    <div class="alert alert-warning" role="alert">
                        Tài khoản đang bị khóa — chỉ xem được hồ sơ và hóa đơn cũ, không thể đặt sân.
                    </div>
                </c:if>

                <form method="post" action="${ctx}/MainController?action=profile" novalidate>
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>

                    <div class="mb-3">
                        <label class="form-label" for="fullName">Họ và tên</label>
                        <input class="form-input ${not empty fieldErrors.name ? 'is-invalid' : ''}"
                               type="text" id="fullName" name="fullName" maxlength="100"
                               value="<c:out value='${account.fullName}'/>" required>
                        <div class="invalid-feedback"><c:out value="${fieldErrors.name}"/></div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="email">Email</label>
                        <input class="form-input ${not empty fieldErrors.email ? 'is-invalid' : ''}"
                               type="email" id="email" name="email" maxlength="100"
                               value="<c:out value='${account.email}'/>" required>
                        <div class="invalid-feedback"><c:out value="${fieldErrors.email}"/></div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label" for="phone">Số điện thoại</label>
                        <input class="form-input ${not empty fieldErrors.phone ? 'is-invalid' : ''}"
                               type="tel" id="phone" name="phone" maxlength="15"
                               value="<c:out value='${account.phone}'/>" required>
                        <div class="invalid-feedback"><c:out value="${fieldErrors.phone}"/></div>
                    </div>

                    <button class="btn-green" type="submit">Lưu thay đổi thông tin</button>
                </form>
                </div><div class="account-coming-soon"><span class="sz-badge sz-badge-dark">Coming Soon</span><h3>Hoạt Động &amp; Thống Kê</h3><p>Thống kê lượt đặt sân, giờ thi đấu và điểm thành viên đang được phát triển.</p><button type="button" disabled>Xem thống kê</button></div>
        </section>
    </div>
</section>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
