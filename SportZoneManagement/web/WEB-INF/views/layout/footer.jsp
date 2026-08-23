<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
</main>
<c:if test="${footerVariant != 'none'}">
<footer class="footer" id="contact"><div class="shell">
    <div class="footer-grid">
        <div><a class="brand" href="${ctx}/MainController?action=home"><svg viewBox="0 0 48 48" aria-hidden="true"><rect x="2" y="2" width="44" height="44" rx="14" fill="#fff"/><path d="M12 24h24M24 12v24" stroke="#caa54b" stroke-width="1.6"/><path d="M16 14h16a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H16a2 2 0 0 1-2-2V16a2 2 0 0 1 2-2Z" fill="none" stroke="#0b1911" stroke-width="1.5"/><circle cx="24" cy="24" r="3.5" fill="#1b5e3b"/></svg><span>Sport<span class="zone">Zone</span></span></a><p>Tổ hợp thể thao Tennis &amp; Pickleball hàng đầu tại Khu CNC Hòa Lạc. Tiêu chuẩn quốc tế, công nghệ đặt sân thông minh và dịch vụ tận tâm.</p></div>
        <div><h4>Khám Phá</h4><a href="${ctx}/MainController?action=home">Trang chủ</a><a href="${ctx}/MainController?action=courts">Danh sách sân</a><a href="${ctx}/MainController?action=courts">Bảng giá thuê sân</a></div>
        <div><h4>Tài Khoản</h4><c:choose><c:when test="${not empty sessionScope.account}"><a href="${ctx}/MainController?action=profile">Hồ sơ cá nhân</a><a href="${ctx}/MainController?action=bookings">Booking của tôi</a><a href="${ctx}/MainController?action=invoices">Lịch sử hóa đơn</a></c:when><c:otherwise><a href="${ctx}/MainController?action=login">Đăng nhập</a><a href="${ctx}/MainController?action=register">Đăng ký tài khoản</a></c:otherwise></c:choose></div>
        <div><h4>Liên Hệ Trực Tiếp</h4><p>📍 Khu CNC Hòa Lạc, Thạch Thất, Hà Nội</p><p>⏰ 06:00 – 21:00 hằng ngày</p><p>📞 (+84) 901 234 567</p><p>✉️ support@sportzone.vn</p></div>
    </div>
    <div class="footer-bottom"><span>© 2026 <b>SportZone</b>. All rights reserved. Nền tảng đặt sân thể thao thế hệ mới.</span><span>PRJ301 · SE203366 · FPT University Hòa Lạc</span></div>
</div></footer>
</c:if>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
document.documentElement.classList.add('js-enabled');
(function(){var header=document.getElementById('header'),hamburger=document.getElementById('hamb'),mobileMenu=document.getElementById('mobileMenu');if(header){window.addEventListener('scroll',function(){header.classList.toggle('scrolled',window.scrollY>24);},{passive:true});}if(hamburger&&mobileMenu){function toggleMenu(force){var open=typeof force==='boolean'?force:!mobileMenu.classList.contains('open');mobileMenu.classList.toggle('open',open);document.body.classList.toggle('menu-open',open);hamburger.setAttribute('aria-expanded',String(open));mobileMenu.setAttribute('aria-hidden',String(!open));hamburger.textContent=open?'×':'☰';}hamburger.addEventListener('click',function(){toggleMenu();});mobileMenu.querySelectorAll('a').forEach(function(link){link.addEventListener('click',function(){toggleMenu(false);});});}var revealElements=document.querySelectorAll('[data-reveal]');function reveal(){var h=window.innerHeight;revealElements.forEach(function(el){if(el.getBoundingClientRect().top<h-80){el.classList.add('show');}});}window.addEventListener('scroll',reveal,{passive:true});reveal();})();
</script>
</body>
</html>
