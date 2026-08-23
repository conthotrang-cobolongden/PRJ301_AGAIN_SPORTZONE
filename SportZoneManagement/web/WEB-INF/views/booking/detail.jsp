<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- U12: Chi tiet booking cua chinh user --%>

<c:set var="pageTitle" value="Chi Tiết Booking #${booking.bookingId} — SportZone" scope="request"/>
<c:set var="pageCss" value="booking-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-booking" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- ==============================================
     BOOKING DETAIL (U12) — SPORTZONE SYNC
     ============================================== -->
<div class="container view-section active" id="viewDetail" style="padding-top:40px; padding-bottom:60px;">
    <!-- Back to bookings link -->
    <a class="btn-back" href="${ctx}/MainController?action=bookings" style="display:inline-flex; align-items:center; gap:8px; font-size:13.5px; font-weight:800; color:var(--green); margin-bottom:20px; text-decoration:none;">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="m15 18-6-6 6-6"/>
        </svg>
        <span>Quay lại danh sách booking</span>
    </a>

    <c:if test="${not empty success}">
        <div class="alert alert-success" role="alert" style="border-radius:14px; margin-bottom:24px;">
            <c:out value="${success}"/>
        </div>
    </c:if>

    <!-- Header info with Badge -->
    <div class="booking-detail-head">
        <div>
            <div class="booking-code">MÃ ĐƠN HÀNG #<c:out value="${booking.bookingId}"/></div>
            <h1>
                Chi Tiết Đơn Đặt Sân
                <c:choose>
                    <c:when test="${booking.status == 'Pending'}"><span class="sz-badge sz-badge-pending">Chờ xác nhận</span></c:when>
                    <c:when test="${booking.status == 'Confirmed'}"><span class="sz-badge sz-badge-confirmed">Đã xác nhận</span></c:when>
                    <c:when test="${booking.status == 'Completed'}"><span class="sz-badge sz-badge-completed">Hoàn thành</span></c:when>
                    <c:when test="${booking.status == 'Cancelled'}"><span class="sz-badge sz-badge-cancelled">Đã hủy</span></c:when>
                    <c:otherwise><span class="sz-badge sz-badge-none">Không đến</span></c:otherwise>
                </c:choose>
            </h1>
        </div>
    </div>

    <div class="booking-detail-grid">
        <!-- ===== LEFT CARD: BUỔI CHƠI & DỊCH VỤ ===== -->
        <div>
            <div class="sz-card" style="padding:32px;">
                <h2 style="font-size:19px; font-weight:800; margin-bottom:20px; color:var(--ink); border-bottom:1px solid var(--line); padding-bottom:12px;">
                    Thông Tin Buổi Chơi
                </h2>

                <dl style="margin-bottom:28px;">
                    <div class="sz-kv">
                        <dt>Sân thi đấu</dt>
                        <dd>
                            <c:out value="${booking.courtName}"/>
                            <span class="sz-badge ${booking.typeSlug == 'tennis' ? 'sz-badge-tennis' : 'sz-badge-pickle'} ms-1">
                                <c:out value="${booking.typeName}"/>
                            </span>
                        </dd>
                    </div>

                    <div class="sz-kv">
                        <dt>Vị trí</dt>
                        <dd><c:out value="${booking.location}"/></dd>
                    </div>

                    <div class="sz-kv">
                        <dt>Ngày thi đấu</dt>
                        <dd><strong><fmt:formatDate value="${booking.slotDateValue}" pattern="dd/MM/yyyy"/></strong></dd>
                    </div>

                    <div class="sz-kv">
                        <dt>Khung giờ</dt>
                        <dd>
                            <strong>
                                <fmt:formatDate value="${booking.startTimeValue}" pattern="HH:mm"/> – <fmt:formatDate value="${booking.endTimeValue}" pattern="HH:mm"/>
                            </strong>
                        </dd>
                    </div>

                    <div class="sz-kv">
                        <dt>Thời gian tạo đơn</dt>
                        <dd><fmt:formatDate value="${booking.bookingDateValue}" pattern="dd/MM/yyyy HH:mm"/></dd>
                    </div>
                </dl>

                <h2 style="font-size:17px; font-weight:800; margin-bottom:16px; color:var(--ink); border-bottom:1px solid var(--line); padding-bottom:10px;">
                    Dịch Vụ Kèm Theo
                </h2>

                <c:choose>
                    <c:when test="${not empty serviceLines}">
                        <dl style="margin-bottom:0;">
                            <c:forEach var="line" items="${serviceLines}">
                                <div class="sz-kv">
                                    <dt>
                                        <c:out value="${line.serviceName}"/>
                                        <span class="text-muted fw-bold">× <c:out value="${line.quantity}"/></span>
                                    </dt>
                                    <dd>
                                        <fmt:formatNumber value="${line.lineTotal}" type="number" groupingUsed="true"/> đ
                                    </dd>
                                </div>
                            </c:forEach>
                        </dl>
                    </c:when>
                    <c:otherwise>
                        <p class="text-muted mb-0" style="font-size:13.5px;">Không sử dụng dịch vụ kèm theo.</p>
                    </c:otherwise>
                </c:choose>

                <c:if test="${not empty booking.notes}">
                    <hr style="margin:24px 0 16px; border-color:var(--line);">
                    <p style="font-size:13px; font-weight:800; color:var(--muted); margin-bottom:6px; text-transform:uppercase; letter-spacing:.05em;">
                        Ghi chú yêu cầu
                    </p>
                    <p style="font-size:14px; margin-bottom:0; color:var(--ink); font-style:italic;">
                        "<c:out value="${booking.notes}"/>"
                    </p>
                </c:if>
            </div>
        </div>

        <!-- ===== RIGHT CARD: TỔNG TIỀN & THANH TOÁN ===== -->
        <div>
            <div class="sz-card" style="padding:32px;">
                <h2 style="font-size:19px; font-weight:800; margin-bottom:20px; color:var(--ink); border-bottom:1px solid var(--line); padding-bottom:12px;">
                    Tổng Chi Phí
                </h2>

                <dl style="margin-bottom:24px;">
                    <div class="sz-kv">
                        <dt>Tiền thuê sân</dt>
                        <dd><fmt:formatNumber value="${booking.courtPriceAtOrder}" type="number" groupingUsed="true"/> đ</dd>
                    </div>

                    <div class="sz-kv">
                        <dt>Tiền dịch vụ</dt>
                        <dd><fmt:formatNumber value="${booking.serviceTotalAtOrder}" type="number" groupingUsed="true"/> đ</dd>
                    </div>

                    <div class="sz-kv sz-kv-total">
                        <dt>Tổng cộng</dt>
                        <dd><fmt:formatNumber value="${booking.totalAmount}" type="number" groupingUsed="true"/> đ</dd>
                    </div>
                </dl>

                <h2 style="font-size:17px; font-weight:800; margin-bottom:14px; color:var(--ink); border-bottom:1px solid var(--line); padding-bottom:10px;">
                    Trạng Thái Thanh Toán
                </h2>

                <div style="margin-bottom:20px;">
                    <c:choose>
                        <c:when test="${booking.paymentStatus == 'Paid'}">
                            <span class="sz-badge sz-badge-paid">Đã thanh toán</span>
                        </c:when>
                        <c:when test="${booking.paymentStatus == 'Unpaid'}">
                            <span class="sz-badge sz-badge-unpaid">Chưa thanh toán</span>
                        </c:when>
                        <c:when test="${booking.paymentStatus == 'Cancelled'}">
                            <span class="sz-badge sz-badge-cancelled">Hóa đơn đã hủy</span>
                        </c:when>
                        <c:otherwise>
                            <span class="sz-badge sz-badge-none">Chưa có hóa đơn</span>
                            <p class="text-muted small mt-2 mb-0" style="line-height:1.5;">
                                Bạn có thể chuyển khoản trực tuyến ngay để tạo hóa đơn điện tử và giữ chỗ an toàn.
                            </p>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div style="margin-top:24px;">
                    <c:choose>
                        <c:when test="${booking.paymentStatus == 'Paid'}">
                            <a class="btn-action-green booking-submit" href="${ctx}/MainController?action=invoiceDetail&amp;bookingId=${booking.bookingId}">
                                Xem Hóa Đơn Điện Tử →
                            </a>
                        </c:when>
                        <c:when test="${booking.paymentStatus == 'Cancelled'}">
                            <button class="btn-action" type="button" disabled style="width:100%; opacity:.6;">Thanh toán đã hủy</button>
                        </c:when>
                        <c:otherwise>
                            <a class="btn-action-green booking-submit" href="${ctx}/MainController?action=invoicePayment&amp;bookingId=${booking.bookingId}">
                                Thanh Toán Chuyển Khoản Ngay →
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="alert alert-info mt-3" style="border-radius:14px; font-size:13px; line-height:1.6;">
                💡 <strong>Lưu ý:</strong> Xuất trình mã booking khi đến nhận sân tại quầy lễ tân SportZone TP.HCM.
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
