<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Admin Executive Suite — SportZone" scope="request"/>
<c:set var="pageCss" value="admin-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-admin" scope="request"/>
<c:set var="headerVariant" value="none" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<c:set var="adminSection" value="dashboard" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="/WEB-INF/views/layout/admin-sidebar.jsp"/>

<div class="admin-viewport">
    <!-- Topbar Header -->
    <header class="admin-topbar">
        <div class="topbar-left">
            <button class="btn-topbar-toggle" id="topbarToggle" type="button" title="Mở rộng / Thu gọn Sidebar">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="3" y1="12" x2="21" y2="12"/>
                    <line x1="3" y1="6" x2="21" y2="6"/>
                    <line x1="3" y1="18" x2="21" y2="18"/>
                </svg>
            </button>
            <div class="court-live-ticker">
                <span style="display:inline-block;width:8px;height:8px;border-radius:50%;background:#22c55e;margin-right:8px;box-shadow:0 0 0 3px rgba(34,197,94,.2);"></span>
                <strong>Hệ Thống Sân TP.HCM:</strong>&nbsp;10 Sân Đang Sẵn Sàng
            </div>
        </div>

        <div class="topbar-right">
            <a href="${ctx}/MainController?action=bookingCreate" class="btn-admin-primary" style="padding:9px 20px; font-size:13px; font-weight:800; border-radius:999px; background:linear-gradient(135deg,#1b5e3b,#13472d); color:#fff; text-decoration:none; box-shadow:0 4px 14px rgba(27,94,59,.25);">
                + Đặt Sân Vãng Lai
            </a>
        </div>
    </header>

    <!-- Main Scrollable Dashboard Content -->
    <main class="admin-main-scroll" style="flex:1; overflow-y:auto; padding:32px 36px;">
        <section class="admin-view active">
            <!-- Page Header -->
            <div class="admin-pagehead" style="display:flex; justify-content:space-between; align-items:flex-end; margin-bottom:32px; flex-wrap:wrap; gap:16px;">
                <div>
                    <span class="page-eyebrow" style="font-size:11px; font-weight:800; letter-spacing:.12em; text-transform:uppercase; color:var(--green); margin-bottom:8px; display:block;">
                        SPORTZONE CONTROL CENTER
                    </span>
                    <h1 style="font-size:32px; color:var(--ink); margin:0;">
                        Bảng Điều Hành <span class="accent-text" style="background:linear-gradient(135deg,#1b5e3b,#caa54b); -webkit-background-clip:text; -webkit-text-fill-color:transparent;">Hệ Thống</span>
                    </h1>
                    <p style="color:var(--muted); font-size:14px; margin:8px 0 0;">
                        Tổng quan dữ liệu vận hành, doanh thu thanh toán và lịch đặt sân toàn tổ hợp.
                    </p>
                </div>
                <div style="display:flex; gap:10px;">
                    <a class="btn-admin-secondary" href="${ctx}/MainController?action=adminCourts" style="padding:9px 18px; font-size:13px; font-weight:800; border-radius:999px; border:1px solid var(--line); background:#fff; color:var(--ink); text-decoration:none;">
                        Quản Lý Sân
                    </a>
                    <a class="btn-admin-primary" href="${ctx}/MainController?action=adminBookings" style="padding:9px 18px; font-size:13px; font-weight:800; border-radius:999px; background:var(--ink); color:#fff; text-decoration:none;">
                        Xem Toàn Bộ Bookings
                    </a>
                </div>
            </div>

            <!-- KPI Metric Grid -->
            <div class="kpi-grid" style="display:grid; grid-template-columns:repeat(4,1fr); gap:20px; margin-bottom:32px;">
                <article class="kpi-card" style="background:#fff; border-radius:var(--radius); border:1px solid var(--line); padding:24px; box-shadow:var(--shadow-sm);">
                    <span class="lbl" style="font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); display:block; margin-bottom:10px;">TỔNG TÀI KHOẢN</span>
                    <strong class="val" style="font-size:32px; font-family:'Montserrat',sans-serif; font-weight:900; color:var(--ink); display:block; line-height:1; margin-bottom:8px;">
                        ${dashboard.totalAccounts}
                    </strong>
                    <small style="color:var(--muted); font-size:12px;">Người dùng &amp; quản trị viên</small>
                </article>

                <article class="kpi-card" style="background:#fff; border-radius:var(--radius); border:1px solid var(--line); padding:24px; box-shadow:var(--shadow-sm);">
                    <span class="lbl" style="font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); display:block; margin-bottom:10px;">SÂN HOẠT ĐỘNG</span>
                    <strong class="val" style="font-size:32px; font-family:'Montserrat',sans-serif; font-weight:900; color:var(--green); display:block; line-height:1; margin-bottom:8px;">
                        ${dashboard.totalCourts}
                    </strong>
                    <small style="color:var(--muted); font-size:12px;">Tennis &amp; Pickleball đạt chuẩn</small>
                </article>

                <article class="kpi-card" style="background:#fff; border-radius:var(--radius); border:1px solid var(--line); padding:24px; box-shadow:var(--shadow-sm);">
                    <span class="lbl" style="font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); display:block; margin-bottom:10px;">TỔNG BOOKINGS</span>
                    <strong class="val" style="font-size:32px; font-family:'Montserrat',sans-serif; font-weight:900; color:#0284c7; display:block; line-height:1; margin-bottom:8px;">
                        ${dashboard.totalBookings}
                    </strong>
                    <small style="color:var(--muted); font-size:12px;">Lịch đặt sân qua hệ thống</small>
                </article>

                <article class="kpi-card" style="background:#fff; border-radius:var(--radius); border:1px solid var(--line); padding:24px; box-shadow:var(--shadow-sm);">
                    <span class="lbl" style="font-size:11px; font-weight:800; letter-spacing:.08em; text-transform:uppercase; color:var(--muted); display:block; margin-bottom:10px;">TỔNG DOANH THU PAID</span>
                    <strong class="val" style="font-size:26px; font-family:'Montserrat',sans-serif; font-weight:900; color:var(--green); display:block; line-height:1; margin-bottom:8px;">
                        <fmt:formatNumber value="${dashboard.totalRevenue}" groupingUsed="true"/> đ
                    </strong>
                    <small style="color:var(--muted); font-size:12px;">Hóa đơn đã thanh toán thành công</small>
                </article>
            </div>

            <!-- Operations Section Grid -->
            <div style="display:grid; grid-template-columns:1.5fr 1fr; gap:24px;">
                <section class="admin-panel" style="background:#fff; border-radius:var(--radius); border:1px solid var(--line); padding:28px; box-shadow:var(--shadow-sm);">
                    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px; border-bottom:1px solid var(--line); padding-bottom:14px;">
                        <div>
                            <h2 style="font-size:18px; font-weight:800; margin:0; color:var(--ink);">Điều Hướng Vận Hành Nhanh</h2>
                            <p style="font-size:13px; color:var(--muted); margin:4px 0 0;">Truy cập nhanh các nghiệp vụ quản lý sân bãi và tiếp đón khách.</p>
                        </div>
                    </div>

                    <div style="display:grid; grid-template-columns:1fr 1fr; gap:16px;">
                        <a href="${ctx}/MainController?action=adminBookings" style="display:block; padding:20px; border-radius:14px; background:var(--cream); border:1px solid var(--line); text-decoration:none; transition:all .2s;">
                            <div style="font-weight:800; font-size:15px; color:var(--ink); margin-bottom:6px;">Quản Lý Bookings →</div>
                            <div style="font-size:13px; color:var(--muted); line-height:1.5;">Kiểm tra danh sách đặt sân, lọc trạng thái và check-in cho khách đến sân.</div>
                        </a>

                        <a href="${ctx}/MainController?action=adminCourts" style="display:block; padding:20px; border-radius:14px; background:var(--cream); border:1px solid var(--line); text-decoration:none; transition:all .2s;">
                            <div style="font-weight:800; font-size:15px; color:var(--ink); margin-bottom:6px;">Quản Lý Sân &amp; Sinh Slot →</div>
                            <div style="font-size:13px; color:var(--muted); line-height:1.5;">Cập nhật giá giờ, trạng thái bảo trì và tự động sinh slot theo tuần.</div>
                        </a>
                    </div>
                </section>

                <section class="admin-panel" style="background:#fff; border-radius:var(--radius); border:1px solid var(--line); padding:28px; box-shadow:var(--shadow-sm);">
                    <div style="margin-bottom:20px; border-bottom:1px solid var(--line); padding-bottom:14px;">
                        <h2 style="font-size:18px; font-weight:800; margin:0; color:var(--ink);">Thông Tin Cơ Sở Vận Hành</h2>
                        <p style="font-size:13px; color:var(--muted); margin:4px 0 0;">Tổ hợp SportZone TP. Hồ Chí Minh</p>
                    </div>

                    <div style="display:flex; flex-direction:column; gap:12px; font-size:13.5px;">
                        <div style="display:flex; justify-content:space-between; padding:8px 0; border-bottom:1px solid rgba(11,25,17,.05);">
                            <span style="color:var(--muted);">Địa chỉ tổ hợp</span>
                            <strong>Khu Công Nghệ Cao, TP. Thủ Đức, TP.HCM</strong>
                        </div>
                        <div style="display:flex; justify-content:space-between; padding:8px 0; border-bottom:1px solid rgba(11,25,17,.05);">
                            <span style="color:var(--muted);">Quy mô sân</span>
                            <strong>10 Sân (Tennis &amp; Pickleball)</strong>
                        </div>
                        <div style="display:flex; justify-content:space-between; padding:8px 0; border-bottom:1px solid rgba(11,25,17,.05);">
                            <span style="color:var(--muted);">Khung giờ hoạt động</span>
                            <strong>06:00 – 22:00 Hàng ngày</strong>
                        </div>
                    </div>
                </section>
            </div>
        </section>
    </main>
</div>

<script>
    (function () {
        var sidebar = document.getElementById('adminSidebar');
        if (!sidebar) return;
        function toggle() {
            document.body.classList.toggle('sidebar-collapsed');
        }
        var sToggle = document.getElementById('sidebarToggle');
        var tToggle = document.getElementById('topbarToggle');
        if (sToggle) sToggle.addEventListener('click', toggle);
        if (tToggle) tToggle.addEventListener('click', toggle);
    })();
</script>
