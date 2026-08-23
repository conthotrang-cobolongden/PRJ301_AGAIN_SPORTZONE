<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Thanh toán chuyển khoản — SportZone" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<section class="container sz-section"><div class="mx-auto" style="max-width:620px">
    <div class="sz-pagehead"><div><p class="sz-kicker sz-kicker-dark mb-1">QR giả lập</p><h1>Thanh toán chuyển khoản</h1></div></div>
    <div class="sz-card text-center"><c:choose>
        <c:when test="${invoice.paymentStatus == 'Cancelled'}">
            <div class="alert alert-secondary mb-3">Thanh toán đã bị hủy hoặc hết hạn.</div>
            <a class="btn btn-sz-outline" href="${ctx}/MainController?action=bookingDetail&amp;id=${invoice.bookingId}">Về booking</a>
        </c:when>
        <c:otherwise>
            <img src="${ctx}/assets/img/payment-qr.svg" width="220" height="220" alt="QR thanh toán giả lập" class="img-fluid border rounded p-2 bg-white"/>
            <p class="mt-3 mb-1">Số tiền</p><p class="h3 fw-bold" style="color:var(--sz-green)"><fmt:formatNumber value="${invoice.finalAmount}" groupingUsed="true"/> đ</p>
            <p>Thời gian còn lại: <strong id="paymentCountdown" data-expires-at="${invoice.expiresAtEpochMilli}">15:00</strong></p>
            <div class="d-flex gap-2 justify-content-center flex-wrap">
                <form method="post" action="${ctx}/MainController?action=invoicePayment"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/><input type="hidden" name="bookingId" value="${invoice.bookingId}"/><input type="hidden" name="operation" value="pay"/><button class="btn btn-sz" type="submit">Thanh toán</button></form>
                <form id="cancelPaymentForm" method="post" action="${ctx}/MainController?action=invoicePayment"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/><input type="hidden" name="bookingId" value="${invoice.bookingId}"/><input type="hidden" name="operation" value="cancel"/><button class="btn btn-outline-danger" type="submit">Hủy thanh toán</button></form>
            </div>
        </c:otherwise>
    </c:choose></div>
</div></section>
<c:if test="${invoice.paymentStatus == 'Unpaid'}"><script>
(function(){var o=document.getElementById('paymentCountdown'),f=document.getElementById('cancelPaymentForm'),e=Number(o.getAttribute('data-expires-at')),s=false;function tick(){var x=Math.max(0,Math.ceil((e-Date.now())/1000)),m=Math.floor(x/60);o.textContent=String(m).padStart(2,'0')+':'+String(x%60).padStart(2,'0');if(x===0&&!s){s=true;f.submit();}}tick();window.setInterval(tick,1000);})();
</script></c:if>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
