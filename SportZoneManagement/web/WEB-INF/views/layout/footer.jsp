<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
</main>
<div class="container pb-4">
    <footer class="sz-footer-luxury">
        <div class="row g-4">
            <div class="col-lg-5">
                <div class="d-flex align-items-center gap-2 mb-3">
                    <span class="sz-mark">SZ</span>
                    <span class="fw-bold fs-4 text-white font-heading">SportZone</span>
                </div>
                <p class="small mb-3 text-slate-400" style="max-width:380px; line-height:1.6">
                    Hệ thống đặt sân Tennis &amp; Pickleball hàng đầu — trải nghiệm đỉnh cao, giữ khung giờ vàng và thanh toán tiện lợi tại quầy.
                </p>
                <div class="d-flex gap-2">
                    <span class="sz-badge sz-badge-gold"><i class="bi bi-shield-check"></i> Cam kết 100% giữ chỗ</span>
                    <span class="sz-badge sz-badge-green"><i class="bi bi-clock-history"></i> Mở cửa 06:00 - 21:00</span>
                </div>
            </div>
            <div class="col-lg-3 col-6 ms-lg-auto">
                <h6>Điều hướng nhanh</h6>
                <div class="d-flex flex-column gap-2">
                    <a href="${ctx}/home"><i class="bi bi-chevron-right me-1 small"></i>Trang chủ</a>
                    <a href="${ctx}/courts"><i class="bi bi-chevron-right me-1 small"></i>Danh sách sân</a>
                    <c:choose>
                        <c:when test="${not empty sessionScope.account}">
                            <a href="${ctx}/bookings"><i class="bi bi-chevron-right me-1 small"></i>Booking của tôi</a>
                            <a href="${ctx}/invoices"><i class="bi bi-chevron-right me-1 small"></i>Hóa đơn</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${ctx}/login"><i class="bi bi-chevron-right me-1 small"></i>Đăng nhập</a>
                            <a href="${ctx}/register"><i class="bi bi-chevron-right me-1 small"></i>Đăng ký tài khoản</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="col-lg-3 col-6">
                <h6>Thông tin liên hệ</h6>
                <div class="d-flex flex-column gap-2.5 small text-slate-300">
                    <div class="d-flex align-items-center gap-2">
                        <i class="bi bi-geo-alt-fill text-warning"></i>
                        <span>Khu CNC Hòa Lạc, Thạch Thất, Hà Nội</span>
                    </div>
                    <div class="d-flex align-items-center gap-2">
                        <i class="bi bi-telephone-fill text-warning"></i>
                        <span>(+84) 901 234 567</span>
                    </div>
                    <div class="d-flex align-items-center gap-2">
                        <i class="bi bi-envelope-fill text-warning"></i>
                        <span>support@sportzone.vn</span>
                    </div>
                    <div class="d-flex align-items-center gap-2">
                        <i class="bi bi-clock-fill text-warning"></i>
                        <span>06:00 – 21:00 hằng ngày</span>
                    </div>
                </div>
            </div>
        </div>
        <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 sz-footer-divider small">
            <span>© 2026 <b>SportZone</b> — Nền tảng đặt sân thể thao thế hệ mới. All rights reserved.</span>
            <span class="badge bg-dark border border-secondary text-secondary font-monospace">PRJ301 · SE203366</span>
        </div>
    </footer>
</div>

<!-- Bootstrap 5 Bundle JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- Smooth Scroll Reveal Script -->
<script>
document.addEventListener("DOMContentLoaded", function() {
    const revealElements = document.querySelectorAll("[data-reveal]");
    
    const revealOnScroll = function() {
        const windowHeight = window.innerHeight;
        revealElements.forEach(function(el) {
            const elementTop = el.getBoundingClientRect().top;
            const elementVisible = 100;
            if (elementTop < windowHeight - elementVisible) {
                el.classList.add("reveal-visible");
            }
        });
    };
    
    window.addEventListener("scroll", revealOnScroll);
    revealOnScroll(); // Run once on page load
});
</script>
</body>
</html>
