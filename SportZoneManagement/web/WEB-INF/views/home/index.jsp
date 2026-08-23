<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="SportZone — Tổ Hợp Thể Thao Tennis &amp; Pickleball Cao Cấp" scope="request"/>
<c:set var="pageCss" value="home-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-home" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="hero" id="home">
    <div class="shell hero-grid">
        <div data-reveal>
            <div class="kicker"><span class="dot"></span> SportZone · Tổ Hợp Tennis &amp; Pickleball Cao Cấp</div>
            <h1>Khung Giờ Vàng.<br><span class="accent-text">Sẵn Sàng Ra Sân.</span></h1>
            <p class="lead">Hệ thống ${tennisCourtCount + pickleballCourtCount} sân Tennis &amp; Pickleball tiêu chuẩn quốc tế tại Khu CNC Hòa Lạc. Tra cứu lịch trống theo thời gian thực, giữ chỗ tức thì và tận hưởng không gian thể thao đỉnh cao.</p>
            <div class="hero-actions"><a class="btn primary" href="${ctx}/MainController?action=courts">Khám phá sân trống <span>→</span></a><c:if test="${empty sessionScope.account}"><a class="btn ghost" href="${ctx}/MainController?action=register">Tạo tài khoản miễn phí</a></c:if></div>
            <div class="trust-row"><span><i>✓</i> Đặt sân trong 60 giây</span><span><i>✓</i> Giữ chỗ theo thời gian thực</span><span><i>✓</i> Mở cửa 06:00 – 21:00</span></div>
        </div>
        <div class="hero-visual" data-reveal>
            <div class="photo-main"><img src="https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?auto=format&amp;fit=crop&amp;w=1400&amp;q=88" alt="Sân thể thao SportZone"><div class="photo-overlay"></div></div>
            <div class="court-coordinate">21.0122° N · 105.5272° E<br><b>KHU CNC HÒA LẠC</b></div>
            <div class="hero-stat-floating"><div><span class="eyebrow">HỆ THỐNG SÂN</span><div class="stat-num">${tennisCourtCount + pickleballCourtCount}</div></div><div class="stat-line"></div><div><b>${tennisCourtCount} Tennis</b><span>${pickleballCourtCount} Pickleball</span></div></div>
        </div>
    </div>
</section>

<div class="ticker" aria-label="Điểm nổi bật"><div class="ticker-track"><span>ĐẶT SÂN NHANH</span><span>LỊCH TRỐNG THỜI GIAN THỰC</span><span>MẶT SÂN ACRYLIC</span><span>ÁNH SÁNG CHỐNG LÓA</span><span>DỊCH VỤ TẠI QUẦY</span><span>MỞ CỬA MỖI NGÀY</span></div></div>

<section class="gateway" id="gateway" aria-label="Chuyển cảnh vào khu sân thể thao">
    <div class="gateway-stage"><div class="gateway-intro"><span class="eyebrow">SPORTZONE ARENA</span><h2>Bước Vào Trận Đấu Của Bạn.</h2><p>Hai phân khu · Một chuẩn thi đấu quốc tế</p></div>
        <div class="arena-doors"><a class="arena-door door-tennis" href="${ctx}/MainController?action=courts&amp;type=tennis"><div class="door-texture"><span class="zone-no">ZONE A</span><div class="door-copy"><span class="mini">${tennisCourtCount} SÂN NGOÀI TRỜI</span><h3>TENNIS<br>ARENA</h3><span class="enter">BƯỚC VÀO KHU TENNIS →</span></div></div></a><a class="arena-door door-pickle" href="${ctx}/MainController?action=courts&amp;type=pickleball"><div class="door-texture"><span class="zone-no">ZONE B</span><div class="door-copy"><span class="mini">${pickleballCourtCount} SÂN NGOÀI TRỜI</span><h3>PICKLEBALL<br>COMPLEX</h3><span class="enter">BƯỚC VÀO KHU PICKLEBALL →</span></div></div></a></div>
    </div>
</section>

<section class="section courts" id="courts">
    <div class="shell">
        <div class="section-head"><div><span class="eyebrow">HỆ THỐNG SÂN BÃI</span><h2>Hệ Thống ${tennisCourtCount + pickleballCourtCount} Sân Tiêu Chuẩn Quốc Tế.</h2></div><p>Thiết kế dành cho cả vận động viên chuyên nghiệp và cộng đồng yêu thể thao tại Hòa Lạc.</p></div>
        <div class="zone-banners"><a class="zone-card tennis-zone" href="${ctx}/MainController?action=courts&amp;type=tennis"><img src="https://images.unsplash.com/photo-1622279457486-62dcc4a431d6?auto=format&amp;fit=crop&amp;w=1200&amp;q=85" alt="Tennis Zone"><div class="zone-content"><span>ZONE A · ${tennisCourtCount} SÂN</span><h3>Tennis Arena</h3><p>Mặt sân acrylic ngoài trời, hệ thống chiếu sáng chống lóa.</p></div></a><a class="zone-card pickle-zone" href="${ctx}/MainController?action=courts&amp;type=pickleball"><img src="https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?auto=format&amp;fit=crop&amp;w=1200&amp;q=85" alt="Pickleball Zone"><div class="zone-content"><span>ZONE B · ${pickleballCourtCount} SÂN</span><h3>Pickleball Complex</h3><p>Không gian hiện đại, mặt sân bám tốt và phù hợp mọi lứa tuổi.</p></div></a></div>
        <div class="filter-bar"><div><button class="filter-tab active" type="button" disabled>Tất cả sân</button><button class="filter-tab" type="button" disabled>Tennis</button><button class="filter-tab" type="button" disabled>Pickleball</button></div><span class="coming-soon">Coming Soon</span></div>
        <div class="court-grid">
            <c:forEach begin="1" end="10" var="placeholderIndex"><article class="court-item-card is-disabled" aria-disabled="true"><div class="court-thumb skeleton-court"><span class="sz-badge sz-badge-dark">Coming Soon</span></div><div class="court-body"><div class="court-topline"><span>SPORTZONE COURT</span><span>—</span></div><h3>Dữ liệu sân trên trang chủ</h3><p>Danh sách sân động đang được khóa tại trang chủ.</p><button class="btn ghost" type="button" disabled>Coming Soon</button></div></article></c:forEach>
        </div>
        <div class="text-center"><a class="btn primary" href="${ctx}/MainController?action=courts">Xem danh sách sân thực tế <span>→</span></a></div>
    </div>
</section>

<section class="section benefits"><div class="shell"><div class="section-head"><div><span class="eyebrow">LỢI THẾ SPORTZONE</span><h2>Giữ Chỗ Nhanh Chóng · Tối Đa Thời Gian Thi Đấu.</h2></div><p>Mọi chi tiết được thiết kế để bạn tập trung vào trận đấu thay vì thủ tục.</p></div><div class="benefit-grid"><article class="benefit"><span class="n">01</span><h3>Đặt trong 60 giây</h3><p>Chọn sân, ngày và khung giờ trực tiếp trên lịch trống.</p></article><article class="benefit"><span class="n">02</span><h3>Giữ chỗ chắc chắn</h3><p>Booking cập nhật theo thời gian thực, hạn chế hoàn toàn trùng lịch.</p></article><article class="benefit"><span class="n">03</span><h3>Thanh toán linh hoạt</h3><p>Thanh toán QR hoặc tại quầy và nhận hóa đơn điện tử.</p></article><article class="benefit"><span class="n">04</span><h3>Chuẩn thi đấu</h3><p>Mặt sân acrylic, ánh sáng chống lóa và bảo trì định kỳ.</p></article></div></div></section>

<section class="section" id="booking"><div class="shell process-wrap"><div class="process-image"><img src="https://images.unsplash.com/photo-1554068865-24cecd4e34b8?auto=format&amp;fit=crop&amp;w=1200&amp;q=85" alt="Thi đấu tại SportZone"></div><div><span class="eyebrow">QUY TRÌNH ĐẶT SÂN</span><h2>Từ Tra Cứu Đến Ra Sân Chỉ Trong 4 Bước.</h2><div class="steps"><div class="step"><span class="num">01</span><div><h3>Chọn phân khu</h3><p>Tennis hoặc Pickleball.</p></div></div><div class="step"><span class="num">02</span><div><h3>Chọn ngày và giờ</h3><p>Xem lịch trống trong 7 ngày.</p></div></div><div class="step"><span class="num">03</span><div><h3>Xác nhận booking</h3><p>Thêm dịch vụ cần thiết.</p></div></div><div class="step"><span class="num">04</span><div><h3>Thanh toán &amp; ra sân</h3><p>Nhận xác nhận và check-in tại quầy.</p></div></div></div></div></div></section>

<section class="cta"><div class="shell"><div class="cta-box"><div><span class="eyebrow">SẴN SÀNG THI ĐẤU?</span><h2>Giữ Khung Giờ Đẹp Ngay Trước Khi Kín Lịch!</h2></div><a class="btn primary" href="${ctx}/MainController?action=courts">Đặt sân ngay <span>→</span></a></div></div></section>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
