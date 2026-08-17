<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- U12: Chi tiết booking + U13: hủy + U16: trạng thái thanh toán + U17: notice "đã chuyển khoản" --%>
<c:set var="pageTitle" value="Chi tiết booking — SportZone" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<section class="container sz-section">
    <a class="sz-back-link" href="${ctx}/bookings">&larr; Về danh sách booking</a>

    <!-- DEEPSEEK: success = thông báo sau khi đặt/hủy thành công (redirect kèm param) -->
    <c:if test="${not empty success}">
        <div class="alert alert-success" role="alert"><c:out value="${success}"/></div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
    </c:if>

    <div class="sz-pagehead">
        <div>
            <p class="sz-kicker sz-kicker-dark mb-1">Booking #${booking.bookingId}</p>
            <h1 class="d-flex flex-wrap align-items-center gap-2">
                Chi tiết booking
                <c:choose>
                    <c:when test="${booking.status == 'Pending'}"><span class="sz-badge sz-badge-pending">Chờ xác nhận</span></c:when>
                    <c:when test="${booking.status == 'Confirmed'}"><span class="sz-badge sz-badge-confirmed">Đã xác nhận</span></c:when>
                    <c:when test="${booking.status == 'Completed'}"><span class="sz-badge sz-badge-completed">Hoàn thành</span></c:when>
                    <c:when test="${booking.status == 'Cancelled'}"><span class="sz-badge sz-badge-cancelled">Đã hủy</span></c:when>
                    <c:otherwise><span class="sz-badge sz-badge-noshow">Không đến</span></c:otherwise>
                </c:choose>
                <%-- U16: badge thanh toán lấy từ invoice; chưa có HĐ → badge "Chưa có hóa đơn" --%>
                <c:choose>
                    <c:when test="${invoice.paymentStatus == 'Paid'}"><span class="sz-badge sz-badge-paid">Đã thanh toán</span></c:when>
                    <c:when test="${invoice.paymentStatus == 'Unpaid'}"><span class="sz-badge sz-badge-unpaid">Chưa thanh toán</span></c:when>
                    <c:when test="${invoice.paymentStatus == 'Cancelled'}"><span class="sz-badge sz-badge-cancelled">HĐ đã hủy</span></c:when>
                    <c:otherwise><span class="sz-badge sz-badge-none">Chưa có hóa đơn</span></c:otherwise>
                </c:choose>
            </h1>
        </div>
    </div>

    <div class="row g-4">
        <!-- ===== Thông tin buổi chơi ===== -->
        <div class="col-lg-7">
            <div class="sz-card">
                <h2 class="h5 mb-3">Thông tin buổi chơi</h2>
                <dl class="mb-3">
                    <div class="sz-kv"><dt>Sân</dt><dd><c:out value="${booking.courtName}"/> (<c:out value="${booking.typeName}"/>)</dd></div>
                    <div class="sz-kv"><dt>Vị trí</dt><dd><c:out value="${booking.location}"/></dd></div>
                    <div class="sz-kv"><dt>Ngày chơi</dt><dd><fmt:formatDate value="${booking.slotDate}" pattern="EEEE, dd/MM/yyyy"/></dd></div>
                    <div class="sz-kv"><dt>Khung giờ</dt><dd><fmt:formatDate value="${booking.startTime}" pattern="HH:mm"/> – <fmt:formatDate value="${booking.endTime}" pattern="HH:mm"/></dd></div>
                    <div class="sz-kv"><dt>Đặt lúc</dt><dd><fmt:formatDate value="${booking.bookingDate}" pattern="dd/MM/yyyy HH:mm"/></dd></div>
                </dl>

                <h2 class="h6 mb-2">Dịch vụ kèm theo</h2>
                <!-- DEEPSEEK: serviceLines = List<BookingService> kèm serviceName (JOIN Services).
                     Giá snapshot PriceAtOrder / LineTotal (rule 7) — không lấy giá hiện hành. -->
                <c:choose>
                    <c:when test="${not empty serviceLines}">
                        <dl class="mb-0">
                            <c:forEach var="l" items="${serviceLines}">
                                <div class="sz-kv">
                                    <dt><c:out value="${l.serviceName}"/> × ${l.quantity}</dt>
                                    <dd><fmt:formatNumber value="${l.lineTotal}" type="number" groupingUsed="true"/> đ</dd>
                                </div>
                            </c:forEach>
                        </dl>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted mb-0">Không dùng dịch vụ kèm theo.</p>
                    </c:otherwise>
                </c:choose>

                <dl class="mt-3 mb-0">
                    <div class="sz-kv"><dt>Tiền sân</dt><dd><fmt:formatNumber value="${booking.courtPriceAtOrder}" type="number" groupingUsed="true"/> đ</dd></div>
                    <div class="sz-kv"><dt>Tiền dịch vụ</dt><dd><fmt:formatNumber value="${booking.serviceTotalAtOrder}" type="number" groupingUsed="true"/> đ</dd></div>
                    <div class="sz-kv sz-kv-total"><dt>Tổng cộng</dt><dd><fmt:formatNumber value="${booking.totalAmount}" type="number" groupingUsed="true"/> đ</dd></div>
                </dl>
            </div>
        </div>

        <!-- ===== Thanh toán + U17 ===== -->
        <div class="col-lg-5">
            <div class="sz-card mb-4">
                <h2 class="h5 mb-3">Thanh toán</h2>
                <c:choose>
                    <c:when test="${not empty invoice}">
                        <dl class="mb-0">
                            <div class="sz-kv"><dt>Mã hóa đơn</dt><dd class="sz-code"><c:out value="${invoice.invoiceCode}"/></dd></div>
                            <div class="sz-kv">
                                <dt>Trạng thái</dt>
                                <dd>
                                    <c:choose>
                                        <c:when test="${invoice.paymentStatus == 'Paid'}"><span class="sz-badge sz-badge-paid">Đã thanh toán</span></c:when>
                                        <c:when test="${invoice.paymentStatus == 'Unpaid'}"><span class="sz-badge sz-badge-unpaid">Chưa thanh toán</span></c:when>
                                        <c:otherwise><span class="sz-badge sz-badge-cancelled">Đã hủy</span></c:otherwise>
                                    </c:choose>
                                </dd>
                            </div>
                            <%-- U18: phương thức thanh toán chỉ có khi Paid --%>
                            <c:if test="${invoice.paymentStatus == 'Paid'}">
                                <div class="sz-kv"><dt>Phương thức</dt><dd>${invoice.paymentMethod == 'BankTransfer' ? 'Chuyển khoản' : 'Tiền mặt'}</dd></div>
                                <div class="sz-kv"><dt>Thanh toán lúc</dt><dd><fmt:formatDate value="${invoice.paidAt}" pattern="dd/MM/yyyy HH:mm"/></dd></div>
                            </c:if>
                        </dl>
                        <a class="btn btn-outline-success btn-sm mt-3" href="${ctx}/invoice?id=${invoice.invoiceId}">Xem hóa đơn</a>
                    </c:when>
                    <c:otherwise>
                        <p class="mb-0">Booking chưa được lập hóa đơn. Hóa đơn sẽ được lập tại quầy khi bạn thanh toán.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- U17: notice "Tôi đã chuyển khoản" — ẩn khi Frozen / đã Paid / booking không còn hiệu lực -->
            <c:if test="${!sessionScope.frozen && empty invoice && (booking.status == 'Pending' || booking.status == 'Confirmed')}">
                <div class="sz-card sz-card-muted">
                    <h2 class="h6 mb-2">Đã chuyển khoản?</h2>
                    <c:choose>
                        <c:when test="${noticeSent}">
                            <div class="alert alert-info mb-0" role="status">
                                Đã ghi nhận thông báo chuyển khoản của bạn. Quầy sẽ xác nhận và lập hóa đơn trong thời gian sớm nhất.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted small">Gửi thông báo để quầy đối soát và lập hóa đơn cho booking này.</p>
                            <form method="post" action="${ctx}/invoice/notice">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                                <input type="hidden" name="bookingId" value="${booking.bookingId}"/>
                                <div class="mb-2">
                                    <label class="form-label small mb-1" for="txnRef">Mã giao dịch (nếu có)</label>
                                    <input class="form-control form-control-sm" type="text" id="txnRef" name="txnRef" maxlength="100">
                                </div>
                                <button class="btn btn-outline-success btn-sm" type="submit">Tôi đã chuyển khoản</button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>
        </div>
    </div>

    <!-- ===== U13: HỦY BOOKING — chỉ hiện khi canCancel (server tính theo chính sách hủy) ===== -->
    <div class="sz-action-bar">
        <c:choose>
            <c:when test="${canCancel}">
                <form method="post" action="${ctx}/booking/cancel"
                      onsubmit="return confirm('Bạn chắc chắn muốn hủy booking này? Khung giờ sẽ được mở lại cho người khác.');">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                    <input type="hidden" name="bookingId" value="${booking.bookingId}"/>
                    <button class="btn btn-outline-danger" type="submit">Hủy booking</button>
                </form>
                <span class="text-muted small">Chỉ hủy được trước giờ chơi ít nhất 2 giờ.</span>
            </c:when>
            <c:when test="${booking.status == 'Pending' || booking.status == 'Confirmed'}">
                <div class="alert alert-warning mb-0 py-2" role="alert">
                    <c:out value="${empty cancelReason ? 'Không thể hủy booking này nữa.' : cancelReason}"/>
                </div>
            </c:when>
        </c:choose>
    </div>
</section>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
