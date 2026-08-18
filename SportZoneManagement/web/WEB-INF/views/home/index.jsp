<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Trang chủ — UI Light Luxury Edition với Scroll Reveal Animations --%>
<c:set var="pageTitle" value="SportZone — Đặt Sân Tennis & Pickleball Đẳng Cấp" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="container">
    <!-- ===== HERO SECTION (Light Luxury Theme) ===== -->
    <section class="sz-hero-luxury" data-reveal="zoom">
        <div class="hero-court-bg"></div>
        <div class="row g-4 align-items-center position-relative" style="z-index: 2;">
            <div class="col-lg-7" data-reveal="fade-up">
                <div class="d-inline-flex align-items-center gap-2 px-3 py-1.5 rounded-pill mb-3" 
                     style="background: rgba(27, 94, 59, 0.08); border: 1px solid rgba(27, 94, 59, 0.15);">
                    <span class="sz-badge sz-badge-gold"><i class="bi bi-star-fill"></i> PREMIUM</span>
                    <span class="small fw-bold" style="color:var(--sz-green)">Nền tảng đặt sân thế hệ mới</span>
                </div>
                
                <h1 class="font-heading">
                    Đặt sân nhanh,<br/>
                    <span class="text-gradient-green">Chơi đúng giờ.</span>
                </h1>
                
                <p class="sz-lead mb-4">
                    Trải nghiệm hệ thống đặt sân Tennis &amp; Pickleball đạt chuẩn quốc tế. Giữ khung giờ vàng, xác nhận thời gian thực và thanh toán tiện lợi tại quầy.
                </p>
                
                <div class="d-flex gap-3 flex-wrap align-items-center">
                    <a class="btn btn-sz btn-lg" href="${ctx}/courts">
                        <i class="bi bi-calendar2-check-fill"></i> Đặt sân ngay
                    </a>
                    <c:if test="${empty sessionScope.account}">
                        <a class="btn btn-sz-outline btn-lg" href="${ctx}/login">
                            <i class="bi bi-box-arrow-in-right"></i> Đăng nhập
                        </a>
                    </c:if>
                </div>
                
                <div class="d-flex align-items-center gap-4 mt-4 pt-3 border-top border-slate-200">
                    <div class="d-flex align-items-center gap-2 text-slate-600 small fw-bold">
                        <i class="bi bi-check-circle-fill text-success fs-5"></i> Sân acrylic chống lóa
                    </div>
                    <div class="d-flex align-items-center gap-2 text-slate-600 small fw-bold">
                        <i class="bi bi-check-circle-fill text-success fs-5"></i> Giữ chỗ 100%
                    </div>
                </div>
            </div>
            
            <div class="col-lg-5 ms-lg-auto" data-reveal="slide-right" class="delay-2">
                <div class="row g-3">
                    <div class="col-6 col-lg-12">
                        <div class="hero-stat-card">
                            <div class="d-flex justify-content-between align-items-start mb-2">
                                <span class="sz-badge sz-badge-tennis">ZONE A</span>
                                <i class="bi bi-trophy-fill fs-3 text-success"></i>
                            </div>
                            <div class="num">${tennisCourtCount}</div>
                            <div class="lbl">Sân Tennis acrylic ngoài trời</div>
                        </div>
                    </div>
                    <div class="col-6 col-lg-12">
                        <div class="hero-stat-card">
                            <div class="d-flex justify-content-between align-items-start mb-2">
                                <span class="sz-badge sz-badge-pickle">ZONE B</span>
                                <i class="bi bi-lightning-charge-fill fs-3 text-warning"></i>
                            </div>
                            <div class="num" style="color:var(--sz-pickle)">${pickleballCourtCount}</div>
                            <div class="lbl">Sân Pickleball đa năng</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- ===== ƯU ĐIỂM (Why Choose SportZone) ===== -->
    <section class="py-5">
        <div class="text-center mb-5" data-reveal="fade-up">
            <span class="sz-badge sz-badge-gold mb-2"><i class="bi bi-shield-star"></i> TÍNH NĂNG NỔI BẬT</span>
            <h2 class="font-heading fs-1 text-slate-900">Vì sao chọn SportZone?</h2>
            <p class="text-slate-500" style="max-width: 540px; margin: 0 auto;">Trải nghiệm đặt sân thông minh, hiện đại và không tốn thời gian chờ đợi.</p>
        </div>
        
        <div class="row g-4">
            <div class="col-md-3 col-sm-6" data-reveal="fade-up" class="delay-1">
                <div class="sz-card hover-lift h-100">
                    <div class="feature-icon-wrapper bg-icon-gold">
                        <i class="bi bi-lightning-fill"></i>
                    </div>
                    <h3 class="h5 font-heading mb-2">Đặt trong 1 phút</h3>
                    <p class="small text-slate-500 mb-0" style="line-height:1.6">Chọn sân, chọn khung giờ trống và xác nhận ngay lập tức trên hệ thống.</p>
                </div>
            </div>
            
            <div class="col-md-3 col-sm-6" data-reveal="fade-up" class="delay-2">
                <div class="sz-card hover-lift h-100">
                    <div class="feature-icon-wrapper bg-icon-green">
                        <i class="bi bi-shield-check"></i>
                    </div>
                    <h3 class="h5 font-heading mb-2">Giữ chỗ chắc chắn</h3>
                    <p class="small text-slate-500 mb-0" style="line-height:1.6">Khung giờ đã đặt hiển thị theo thời gian thực, đảm bảo không trùng lịch.</p>
                </div>
            </div>
            
            <div class="col-md-3 col-sm-6" data-reveal="fade-up" class="delay-3">
                <div class="sz-card hover-lift h-100">
                    <div class="feature-icon-wrapper bg-icon-orange">
                        <i class="bi bi-credit-card-2-front-fill"></i>
                    </div>
                    <h3 class="h5 font-heading mb-2">Thanh toán linh hoạt</h3>
                    <p class="small text-slate-500 mb-0" style="line-height:1.6">Tiền mặt hoặc chuyển khoản tại quầy, xuất hóa đơn tự động tức thì.</p>
                </div>
            </div>
            
            <div class="col-md-3 col-sm-6" data-reveal="fade-up" class="delay-4">
                <div class="sz-card hover-lift h-100">
                    <div class="feature-icon-wrapper bg-icon-blue">
                        <i class="bi bi-award-fill"></i>
                    </div>
                    <h3 class="h5 font-heading mb-2">Sân chuẩn quốc tế</h3>
                    <p class="small text-slate-500 mb-0" style="line-height:1.6">Mặt sân acrylic, hệ thống đèn chiếu sáng chống lóa và bảo trì định kỳ.</p>
                </div>
            </div>
        </div>
    </section>

    <!-- ===== BẢNG GIÁ & CHỌN LOẠI SÂN ===== -->
    <section class="py-5">
        <div class="text-center mb-5" data-reveal="fade-up">
            <span class="sz-badge sz-badge-green mb-2"><i class="bi bi-tags-fill"></i> BẢNG GIÁ & SÂN BÓNG</span>
            <h2 class="font-heading fs-1">Lựa chọn sân thi đấu của bạn</h2>
            <p class="text-slate-500">Giá thuê tính theo giờ, đã bao gồm hệ thống chiếu sáng tiêu chuẩn.</p>
        </div>
        
        <div class="row g-4 justify-content-center">
            <!-- Tennis Card -->
            <div class="col-md-6 col-lg-5" data-reveal="slide-left">
                <article class="luxury-court-card">
                    <div class="court-visual">
                        <div class="court-visual-bg tennis-bg"></div>
                        <div class="court-lines-overlay"></div>
                        <div class="court-badge-floating">
                            <span class="sz-badge sz-badge-tennis bg-white shadow-sm"><i class="bi bi-circle-fill text-success"></i> TENNIS ZONE A</span>
                        </div>
                        <div class="court-price-tag">
                            150.000 đ <span class="fs-6 fw-normal text-muted">/ giờ</span>
                        </div>
                    </div>
                    <div class="p-4 d-flex flex-column flex-grow-1">
                        <h3 class="h4 font-heading mb-2">Sân Tennis Đạt Chuẩn</h3>
                        <p class="small text-slate-500 mb-3">
                            Hệ thống <b>${tennisCourtCount} sân cứng acrylic</b> ngoài trời cao cấp, phù hợp đánh đơn và đánh đôi chuyên nghiệp.
                        </p>
                        <div class="mt-auto">
                            <a class="btn btn-sz-green w-100" href="${ctx}/courts?type=tennis">
                                Xem danh sách sân Tennis <i class="bi bi-arrow-right"></i>
                            </a>
                        </div>
                    </div>
                </article>
            </div>
            
            <!-- Pickleball Card -->
            <div class="col-md-6 col-lg-5" data-reveal="slide-right">
                <article class="luxury-court-card">
                    <div class="court-visual">
                        <div class="court-visual-bg pickle-bg"></div>
                        <div class="court-lines-overlay"></div>
                        <div class="court-badge-floating">
                            <span class="sz-badge sz-badge-pickle bg-white shadow-sm"><i class="bi bi-circle-fill text-warning"></i> PICKLEBALL ZONE B</span>
                        </div>
                        <div class="court-price-tag">
                            120.000 đ <span class="fs-6 fw-normal text-muted">/ giờ</span>
                        </div>
                    </div>
                    <div class="p-4 d-flex flex-column flex-grow-1">
                        <h3 class="h4 font-heading mb-2">Sân Pickleball Hiện Đại</h3>
                        <p class="small text-slate-500 mb-3">
                            Cụm <b>${pickleballCourtCount} sân ngoài trời</b> thiết kế hiện đại, mặt sân bám tốt, thích hợp mọi lứa tuổi.
                        </p>
                        <div class="mt-auto">
                            <a class="btn btn-sz-outline w-100" href="${ctx}/courts?type=pickleball">
                                Xem danh sách sân Pickleball <i class="bi bi-arrow-right"></i>
                            </a>
                        </div>
                    </div>
                </article>
            </div>
        </div>
    </section>

    <!-- ===== QUY TRÌNH 4 BƯỚC ===== -->
    <section class="py-5 my-3" data-reveal="zoom">
        <div class="glass-card-light rounded-4 p-4 p-md-5">
            <div class="row align-items-center mb-4">
                <div class="col-lg-6">
                    <span class="sz-badge sz-badge-dark mb-2">QUY TRÌNH ĐẶT SÂN</span>
                    <h2 class="font-heading fs-2 mb-1">4 Bước Đơn Giản Ra Sân</h2>
                </div>
                <div class="col-lg-6 text-lg-end">
                    <p class="text-slate-500 small mb-0">Hệ thống tối ưu giúp bạn hoàn thành giữ chỗ nhanh chóng chỉ trong vài thao tác.</p>
                </div>
            </div>
            
            <div class="row g-3">
                <div class="col-md-3 col-6" data-reveal="fade-up" class="delay-1">
                    <div class="process-card">
                        <div class="process-number">01</div>
                        <h3 class="h6 font-heading mb-1">Chọn loại sân</h3>
                        <p class="small text-slate-500 mb-0">Lựa chọn Tennis hoặc Pickleball theo sở thích.</p>
                    </div>
                </div>
                
                <div class="col-md-3 col-6" data-reveal="fade-up" class="delay-2">
                    <div class="process-card">
                        <div class="process-number">02</div>
                        <h3 class="h6 font-heading mb-1">Chọn khung giờ</h3>
                        <p class="small text-slate-500 mb-0">Xem trực quan các slot giờ trống theo ngày.</p>
                    </div>
                </div>
                
                <div class="col-md-3 col-6" data-reveal="fade-up" class="delay-3">
                    <div class="process-card">
                        <div class="process-number">03</div>
                        <h3 class="h6 font-heading mb-1">Xác nhận booking</h3>
                        <p class="small text-slate-500 mb-0">Thêm phụ kiện thuê vợt, bóng hoặc nước uống.</p>
                    </div>
                </div>
                
                <div class="col-md-3 col-6" data-reveal="fade-up" class="delay-4">
                    <div class="process-card">
                        <div class="process-number">04</div>
                        <h3 class="h6 font-heading mb-1">Thanh toán &amp; ra sân</h3>
                        <p class="small text-slate-500 mb-0">Nhận mã đặt sân và thanh toán linh hoạt tại quầy.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
