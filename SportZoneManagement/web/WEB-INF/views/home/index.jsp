<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Trang chủ — UI theo demo kit C1 (hero card + ưu điểm + quy trình) --%>
<c:set var="pageTitle" value="SportZone — Trang chủ" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="container">
    <!-- ===== HERO (demo kit C1) ===== -->
    <section class="sz-hero">
        <div class="court-lines"></div>
        <div class="row g-4 align-items-center position-relative">
            <div class="col-lg-7">
                <div class="sz-hero-pill"><span class="pill-tag">Sport</span> Nền tảng đặt sân thế hệ mới</div>
                <h1>Đặt sân nhanh,<br/>chơi đúng giờ.</h1>
                <p class="sz-lead">Chọn sân Tennis &amp; Pickleball, giữ khung giờ đẹp và thanh toán tại quầy — tất cả trong một phút.</p>
                <div class="sz-hero-actions">
                    <a class="btn btn-sz btn-lg" href="${ctx}/courts">Xem sân</a>
                    <c:if test="${empty sessionScope.account}">
                        <a class="btn btn-sz-ghost btn-lg" href="${ctx}/login">Đăng nhập</a>
                    </c:if>
                </div>
            </div>
            <div class="col-lg-4 ms-lg-auto">
                <div class="row g-3">
                    <div class="col-6 col-lg-12">
                        <div class="sz-hero-stat">
                            <div class="num">${tennisCourtCount}</div>
                            <div class="lbl">Sân Tennis · Zone A</div>
                        </div>
                    </div>
                    <div class="col-6 col-lg-12">
                        <div class="sz-hero-stat">
                            <div class="num">${pickleballCourtCount}</div>
                            <div class="lbl">Sân Pickleball · Zone B</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- ===== ƯU ĐIỂM (demo kit C1) ===== -->
    <section class="sz-section">
        <h2 class="fw-bold mb-1" style="letter-spacing:-0.02em">Vì sao chọn SportZone?</h2>
        <p class="small mb-4" style="color:var(--sz-muted-ink)">Đặt sân không cần gọi điện, không chờ đợi.</p>
        <div class="row g-3">
            <div class="col-md-3 col-6">
                <div class="sz-card hover-lift">
                    <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" viewBox="0 0 24 24" fill="currentColor" style="color:var(--sz-gold)" aria-hidden="true"><path d="M13 2 3 14h7l-1 8 10-12h-7l1-8z"/></svg>
                    <h3 class="h6 mt-2 mb-1">Đặt trong 1 phút</h3>
                    <p class="small mb-0" style="color:var(--sz-muted-ink)">Chọn sân, chọn giờ, xác nhận — xong.</p>
                </div>
            </div>
            <div class="col-md-3 col-6">
                <div class="sz-card hover-lift">
                    <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" viewBox="0 0 24 24" fill="none" stroke="var(--sz-green)" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/><polyline points="9 16 11 18 15 14"/></svg>
                    <h3 class="h6 mt-2 mb-1">Giữ chỗ chắc chắn</h3>
                    <p class="small mb-0" style="color:var(--sz-muted-ink)">Khung giờ đã đặt không bị trùng, hiển thị theo thời gian thực.</p>
                </div>
            </div>
            <div class="col-md-3 col-6">
                <div class="sz-card hover-lift">
                    <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" viewBox="0 0 24 24" fill="none" stroke="var(--sz-pickle)" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/></svg>
                    <h3 class="h6 mt-2 mb-1">Thanh toán linh hoạt</h3>
                    <p class="small mb-0" style="color:var(--sz-muted-ink)">Tiền mặt hoặc chuyển khoản tại quầy, hóa đơn rõ ràng.</p>
                </div>
            </div>
            <div class="col-md-3 col-6">
                <div class="sz-card hover-lift">
                    <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" viewBox="0 0 24 24" fill="none" stroke="var(--sz-tennis)" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/><path d="M4 22h16"/><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/><path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/><path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/></svg>
                    <h3 class="h6 mt-2 mb-1">Sân chuẩn chất lượng</h3>
                    <p class="small mb-0" style="color:var(--sz-muted-ink)">Mặt sân, lưới và ánh sáng được bảo trì định kỳ.</p>
                </div>
            </div>
        </div>
    </section>

    <!-- ===== QUY TRÌNH 4 BƯỚC (demo kit C1) ===== -->
    <section class="sz-card-soft mb-5 p-4">
        <h2 class="fw-bold mb-3" style="letter-spacing:-0.02em">Quy trình 4 bước</h2>
        <div class="row g-3">
            <div class="col-md-3 col-6">
                <div class="bg-white rounded-4 p-3 h-100 shadow-sm">
                    <div class="sz-process-num">01</div>
                    <h3 class="h6">Chọn sân</h3>
                    <p class="small mb-0" style="color:var(--sz-muted-ink)">Tennis hoặc Pickleball, xem giá theo giờ.</p>
                </div>
            </div>
            <div class="col-md-3 col-6">
                <div class="bg-white rounded-4 p-3 h-100 shadow-sm">
                    <div class="sz-process-num">02</div>
                    <h3 class="h6">Chọn khung giờ</h3>
                    <p class="small mb-0" style="color:var(--sz-muted-ink)">Lưới giờ trống hiển thị theo từng ngày.</p>
                </div>
            </div>
            <div class="col-md-3 col-6">
                <div class="bg-white rounded-4 p-3 h-100 shadow-sm">
                    <div class="sz-process-num">03</div>
                    <h3 class="h6">Xác nhận booking</h3>
                    <p class="small mb-0" style="color:var(--sz-muted-ink)">Kèm dịch vụ: thuê vợt, bóng, nước uống.</p>
                </div>
            </div>
            <div class="col-md-3 col-6">
                <div class="bg-white rounded-4 p-3 h-100 shadow-sm">
                    <div class="sz-process-num">04</div>
                    <h3 class="h6">Thanh toán &amp; ra sân</h3>
                    <p class="small mb-0" style="color:var(--sz-muted-ink)">Nhận hóa đơn, đến sân đúng giờ.</p>
                </div>
            </div>
        </div>
    </section>

    <!-- ===== BẢNG GIÁ THEO LOẠI SÂN ===== -->
    <section class="pb-5">
        <div class="text-center mb-4">
            <span class="sz-badge sz-badge-dark mb-2">Bảng giá</span>
            <h2 class="fw-bold mb-1" style="letter-spacing:-0.02em">Chọn sân của bạn</h2>
            <p class="small" style="color:var(--sz-muted-ink)">Giá thuê theo giờ, đã gồm ánh sáng tiêu chuẩn.</p>
        </div>
        <div class="row g-4 justify-content-center">
            <div class="col-md-6 col-lg-5">
                <article class="sz-card sz-card-lg hover-lift text-center p-4">
                    <span class="sz-badge sz-badge-tennis mb-2">Tennis</span>
                    <h3 class="h5">Sân Tennis — Zone A</h3>
                    <p class="small" style="color:var(--sz-muted-ink)">${tennisCourtCount} sân cứng acrylic ngoài trời</p>
                    <div class="display-6 fw-bold">150.000 đ<span class="fs-6 fw-semibold" style="color:var(--sz-muted-ink)"> / giờ</span></div>
                    <a class="btn btn-sz-outline w-100 mt-3" href="${ctx}/courts?type=tennis">Xem sân Tennis</a>
                </article>
            </div>
            <div class="col-md-6 col-lg-5">
                <article class="sz-card sz-card-lg hover-lift text-center p-4">
                    <span class="sz-badge sz-badge-pickle mb-2">Pickleball</span>
                    <h3 class="h5">Sân Pickleball — Zone B</h3>
                    <p class="small" style="color:var(--sz-muted-ink)">${pickleballCourtCount} sân ngoài trời, phù hợp đánh đơn / đôi</p>
                    <div class="display-6 fw-bold">120.000 đ<span class="fs-6 fw-semibold" style="color:var(--sz-muted-ink)"> / giờ</span></div>
                    <a class="btn btn-sz-outline w-100 mt-3" href="${ctx}/courts?type=pickleball">Xem sân Pickleball</a>
                </article>
            </div>
        </div>
    </section>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
