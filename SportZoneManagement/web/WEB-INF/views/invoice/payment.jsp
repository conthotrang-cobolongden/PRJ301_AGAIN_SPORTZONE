<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Thanh toán chuyển khoản — SportZone" scope="request"/>
<c:set var="pageCss" value="invoice-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-invoice" scope="request"/>
<c:set var="headerVariant" value="standard" scope="request"/>
<c:set var="footerVariant" value="none" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<section class="container view-section active"><div class="payment-shell">
    <div class="invoice-page-head"><span class="sz-badge sz-badge-paid">QR THANH TOÁN</span><h1>Thanh Toán Chuyển Khoản</h1><p>Quét mã QR và xác nhận giao dịch cho booking của bạn.</p></div>
    <div class="invoice-sheet payment-card"><c:choose>
        <c:when test="${invoice.paymentStatus == 'Cancelled'}">
            <div class="alert alert-secondary mb-3">Thanh toán đã bị hủy hoặc hết hạn.</div>
            <a class="btn btn-sz-outline" href="${ctx}/MainController?action=${sessionScope.roleName == 'Admin' ? 'adminBookings' : 'bookingDetail'}&amp;id=${invoice.bookingId}">Về booking</a>
        </c:when>
        <c:otherwise>
            <img src="${ctx}/assets/img/payment-qr.svg" width="220" height="220" alt="QR thanh toán giả lập" class="payment-qr"/>
            <p class="mt-3 mb-1">Số tiền</p><p class="h3 fw-bold" style="color:var(--sz-green)"><fmt:formatNumber value="${invoice.finalAmount}" groupingUsed="true"/> đ</p>
            <p>Thời gian còn lại: <strong id="paymentCountdown" data-expires-at="${invoice.expiresAtEpochMilli}">15:00</strong></p>
            <div class="d-flex gap-2 justify-content-center flex-wrap">
                <form method="post" action="${ctx}/MainController?action=invoicePayment"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/><input type="hidden" name="bookingId" value="${invoice.bookingId}"/><input type="hidden" name="operation" value="pay"/><button class="invoice-print-btn" type="submit">Xác Nhận Thanh Toán</button></form>
                <form id="cancelPaymentForm" method="post" action="${ctx}/MainController?action=invoicePayment"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/><input type="hidden" name="bookingId" value="${invoice.bookingId}"/><input type="hidden" name="operation" value="cancel"/><button class="invoice-view-btn danger" type="submit">Hủy thanh toán</button></form>
            </div>
        </c:otherwise>
    </c:choose></div>
</div></section>
<c:if test="${invoice.paymentStatus == 'Unpaid'}"><script>
(function(){var o=document.getElementById('paymentCountdown'),f=document.getElementById('cancelPaymentForm'),e=Number(o.getAttribute('data-expires-at')),s=false;function tick(){var x=Math.max(0,Math.ceil((e-Date.now())/1000)),m=Math.floor(x/60);o.textContent=String(m).padStart(2,'0')+':'+String(x%60).padStart(2,'0');if(x===0&&!s){s=true;f.submit();}}tick();window.setInterval(tick,1000);})();
</script></c:if>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
