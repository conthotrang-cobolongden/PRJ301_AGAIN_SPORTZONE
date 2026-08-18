<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
</main>
<div class="container pb-4">
    <footer class="sz-footer">
        <div class="row g-4">
            <div class="col-lg-5">
                <div class="d-flex align-items-center gap-2 mb-2">
                    <span class="sz-mark" style="margin-right:0">SZ</span>
                    <span class="fw-bold fs-5 text-white">SportZone</span>
                </div>
                <p class="small mb-0">Hệ thống đặt sân Tennis &amp; Pickleball — đặt nhanh, chơi đúng giờ.</p>
            </div>
            <div class="col-lg-3 col-6">
                <h6 class="mb-3">Điều hướng</h6>
                <div class="d-flex flex-column gap-2">
                    <a href="${ctx}/home">Trang chủ</a>
                    <a href="${ctx}/courts">Danh sách sân</a>
                    <c:choose>
                        <c:when test="${not empty sessionScope.account}">
                            <a href="${ctx}/bookings">Booking của tôi</a>
                            <a href="${ctx}/invoices">Hóa đơn</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${ctx}/login">Đăng nhập</a>
                            <a href="${ctx}/register">Đăng ký</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="col-lg-4 col-6">
                <h6 class="mb-3">Liên hệ</h6>
                <div class="d-flex flex-column gap-2 small">
                    <span>Khu CNC Hòa Lạc, Hà Nội</span>
                    <span>(+84) 901 234 567</span>
                    <span>support@sportzone.vn</span>
                    <span>Mở cửa 06:00 – 21:00 hằng ngày</span>
                </div>
            </div>
        </div>
        <div class="d-flex justify-content-between flex-wrap gap-2 mt-4 pt-3 small sz-footer-line">
            <span>SportZone — Tennis &amp; Pickleball</span>
            <span>PRJ301 · SE203366</span>
        </div>
    </footer>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
