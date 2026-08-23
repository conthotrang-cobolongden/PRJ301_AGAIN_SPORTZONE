<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Thanh Toán Chuyển Khoản — SportZone" scope="request"/>
<c:set var="pageCss" value="invoice-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-invoice" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- ==============================================
     INVOICE PAYMENT (U15) — SPORTZONE SYNC
     ============================================== -->
<div class="container" style="padding-top:40px; padding-bottom:60px;">
    <div class="payment-shell">
        <div class="invoice-page-head" style="text-align:center; max-width:600px; margin:0 auto 32px;">
            <div class="kicker"><span class="dot"></span> CỔNG THANH TOÁN SPORTZONE</div>
            <h1 style="font-size:32px; color:var(--ink); margin:8px 0;">
                Thanh Toán <span class="accent-text" style="background:linear-gradient(135deg,#1b5e3b,#caa54b); -webkit-background-clip:text; -webkit-text-fill-color:transparent;">Chuyển Khoản QR</span>
            </h1>
            <p style="color:var(--muted); font-size:14.5px; line-height:1.6;">
                Quét mã QR qua ứng dụng ngân hàng và xác nhận giao dịch để giữ chỗ tức thì.
            </p>
        </div>

        <div class="payment-card">
            <c:choose>
                <c:when test="${invoice.paymentStatus == 'Cancelled'}">
                    <div class="alert alert-danger" style="border-radius:14px; margin-bottom:20px; font-weight:700;">
                        Giao dịch thanh toán này đã bị hủy hoặc hết hạn.
                    </div>
                    <a class="btn green" href="${ctx}/MainController?action=${sessionScope.roleName == 'Admin' ? 'adminBookings' : 'bookingDetail'}&amp;id=${invoice.bookingId}">
                        ← Quay lại thông tin booking
                    </a>
                </c:when>

                <c:otherwise>
                    <!-- QR Frame -->
                    <div style="display:inline-block; padding:16px; background:#fff; border-radius:20px; border:2px solid var(--line); box-shadow:0 8px 24px rgba(11,25,17,.06); margin-bottom:20px;">
                        <img src="${ctx}/assets/img/payment-qr.svg" width="220" height="220" alt="QR thanh toán giả lập" style="display:block; border-radius:12px;"/>
                    </div>

                    <!-- Price -->
                    <p style="font-size:12.5px; font-weight:800; text-transform:uppercase; letter-spacing:.08em; color:var(--muted); margin:0 0 4px;">
                        Số tiền thanh toán
                    </p>
                    <div style="font-family:'Montserrat',sans-serif; font-size:34px; font-weight:900; color:var(--green); margin-bottom:16px;">
                        <fmt:formatNumber value="${invoice.finalAmount}" groupingUsed="true"/> đ
                    </div>

                    <!-- Countdown Timer -->
                    <div style="display:inline-flex; align-items:center; gap:8px; background:var(--cream); padding:8px 20px; border-radius:999px; font-size:13.5px; font-weight:700; color:var(--ink); border:1px solid var(--line); margin-bottom:28px;">
                        <span style="color:var(--muted);">Thời gian giữ chỗ còn lại:</span>
                        <strong id="paymentCountdown" data-expires-at="${invoice.expiresAtEpochMilli}" style="color:var(--danger); font-family:'Montserrat',sans-serif; font-weight:900; font-size:15px;">15:00</strong>
                    </div>

                    <!-- Buttons -->
                    <div style="display:flex; flex-direction:column; gap:12px; max-width:380px; margin:0 auto;">
                        <form method="post" action="${ctx}/MainController?action=invoicePayment" style="margin:0;">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                            <input type="hidden" name="bookingId" value="${invoice.bookingId}"/>
                            <input type="hidden" name="operation" value="pay"/>
                            <button class="btn green" type="submit" style="width:100%; padding:14px 24px; font-size:15px; font-weight:800; border-radius:999px; box-shadow:0 6px 20px rgba(27,94,59,.25);">
                                Xác Nhận Đã Chuyển Khoản Thành Công →
                            </button>
                        </form>

                        <form id="cancelPaymentForm" method="post" action="${ctx}/MainController?action=invoicePayment" style="margin:0;">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                            <input type="hidden" name="bookingId" value="${invoice.bookingId}"/>
                            <input type="hidden" name="operation" value="cancel"/>
                            <button type="submit" style="width:100%; padding:12px 20px; font-size:13.5px; font-weight:800; border-radius:999px; background:transparent; color:var(--danger); border:1.5px solid var(--danger-border); cursor:pointer; transition:all .2s;">
                                Hủy giao dịch thanh toán này
                            </button>
                        </form>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<c:if test="${invoice.paymentStatus == 'Unpaid'}">
<script>
    (function () {
        var o = document.getElementById('paymentCountdown');
        var f = document.getElementById('cancelPaymentForm');
        if (!o) return;
        var e = Number(o.getAttribute('data-expires-at'));
        var s = false;
        function tick() {
            var x = Math.max(0, Math.ceil((e - Date.now()) / 1000));
            var m = Math.floor(x / 60);
            o.textContent = String(m).padStart(2, '0') + ':' + String(x % 60).padStart(2, '0');
            if (x === 0 && !s) {
                s = true;
                if (f) f.submit();
            }
        }
        tick();
        window.setInterval(tick, 1000);
    })();
</script>
</c:if>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
