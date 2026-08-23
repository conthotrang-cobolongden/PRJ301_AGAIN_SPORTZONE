<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Đặt Sân — SportZone" scope="request"/>
<c:set var="pageCss" value="booking-design.css" scope="request"/>
<c:set var="pageBodyClass" value="sz-page-booking" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- ==============================================
     BOOKING CREATE (U10) — SPORTZONE CONCEPT SYNC
     ============================================== -->
<div class="container view-section active" id="viewCreate" style="padding-top:40px; padding-bottom:60px;">
    <c:if test="${sessionScope.roleName == 'Admin'}">
        <div class="alert alert-info" style="border-radius:14px; margin-bottom:24px;">Booking này được ghi nhận cho <strong>Khách vãng lai</strong>.</div>
    </c:if>

    <!-- Intro Header with Synchronized Aesthetics -->
    <div class="booking-page-intro">
        <div class="kicker"><span class="dot"></span> ĐẶT SÂN ONLINE TRỰC TUYẾN</div>
        <h1>Đặt Lịch <span class="accent-text">Sân Thi Đấu</span></h1>
        <p>Chọn sân, khung giờ và dịch vụ đi kèm. Giữ chỗ tức thì 100% không lo trùng lịch.</p>
    </div>

    <!-- Frozen account warning -->
    <c:if test="${sessionScope.frozen}">
        <div class="alert alert-danger" role="alert" style="border-radius:14px; margin-bottom:24px;">
            Tài khoản đang bị khóa — không thể đặt sân. Vui lòng liên hệ quầy để được hỗ trợ.
        </div>
    </c:if>

    <!-- Business error message -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert" style="border-radius:14px; margin-bottom:24px;"><c:out value="${error}"/></div>
    </c:if>

    <div class="booking-create-grid">
        <!-- ===== LEFT COLUMN: STEP-BY-STEP FORM ===== -->
        <div>
            <!-- Bước 1+2: chọn sân & ngày — GET reload lại lưới slot -->
            <div class="sz-card mb-4">
                <form method="get" action="${ctx}/MainController">
                    <input type="hidden" name="action" value="bookingCreate"/>
                    <div class="row g-3 align-items-end">
                        <div class="col-md-5">
                            <p class="sz-step-label"><span class="step-num">1</span> Chọn sân đấu</p>
                            <select class="form-select" name="courtId" aria-label="Chọn sân" style="border-radius:10px; padding:10px 14px; font-weight:600;">
                                <c:forEach var="c" items="${courtList}">
                                    <option value="${c.courtId}" data-price="${c.pricePerHour}"
                                            ${c.courtId == selectedCourtId ? 'selected' : ''}>
                                        <c:out value="${c.courtName}"/> — <fmt:formatNumber value="${c.pricePerHour}" type="number" groupingUsed="true"/> đ/giờ
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <p class="sz-step-label"><span class="step-num">2</span> Chọn ngày chơi</p>
                            <input class="form-control" type="date" name="date"
                                   value="${selectedDate}" min="${minDate}" max="${maxDate}" aria-label="Chọn ngày"
                                   style="border-radius:10px; padding:10px 14px; font-weight:600;">
                        </div>
                        <div class="col-md-3 d-grid">
                            <button class="btn btn-outline-success" type="submit" style="padding:10px; font-weight:800; border-radius:10px;">Xem khung giờ</button>
                        </div>
                    </div>
                </form>
            </div>

            <!-- Form POST đặt sân -->
            <form method="post" action="${ctx}/MainController?action=bookingCreate" id="szBookingForm"
                  data-court-price="${selectedCourtPrice}" novalidate>
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                <input type="hidden" name="courtId" value="${selectedCourtId}"/>
                <input type="hidden" name="date" value="${selectedDate}"/>

                <!-- Bước 3: khung giờ -->
                <div class="sz-card mb-4">
                    <p class="sz-step-label"><span class="step-num">3</span> Chọn khung giờ trống</p>
                    <c:choose>
                        <c:when test="${not empty slotList}">
                            <div class="slot-grid-radio">
                                <c:forEach var="s" items="${slotList}">
                                    <c:choose>
                                        <c:when test="${s.status == 'Available'}">
                                            <label class="slot-radio-label">
                                                <input class="sz-slot-radio" type="radio" name="slotId"
                                                       value="${s.timeSlotId}"
                                                       data-start="${s.startTime}"
                                                       data-end="${s.endTime}"
                                                       ${s.timeSlotId == selectedSlotId ? 'checked' : ''}>
                                                <span class="slot-display-time"><c:out value="${s.startTime}"/> – <c:out value="${s.endTime}"/></span>
                                                <span class="slot-display-state">Trống</span>
                                            </label>
                                        </c:when>
                                        <c:when test="${s.status == 'Booked'}">
                                            <span class="slot-radio-label is-booked" title="Đã có người đặt">
                                                <span class="slot-display-time"><c:out value="${s.startTime}"/> – <c:out value="${s.endTime}"/></span>
                                                <span class="slot-display-state">Đã đặt</span>
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="slot-radio-label is-booked" title="Khung giờ đóng hoặc đã qua">
                                                <span class="slot-display-time"><c:out value="${s.startTime}"/> – <c:out value="${s.endTime}"/></span>
                                                <span class="slot-display-state">Đóng</span>
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </div>
                            <div class="sz-slot-legend">
                                <span><span class="swatch swatch-available"></span>Trống</span>
                                <span><span class="swatch swatch-booked"></span>Đã đặt</span>
                                <span><span class="swatch swatch-closed"></span>Đóng / Đã qua</span>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state" style="text-align:center; padding:30px 10px;">
                                <span class="sz-badge sz-badge-pickle" style="margin-bottom:10px;">THÔNG BÁO</span>
                                <h3 style="font-size:18px; margin-bottom:6px;">Chưa mở khung giờ ngày này</h3>
                                <p style="color:var(--muted); font-size:13.5px;">Chọn sân và ngày ở trên rồi bấm "Xem khung giờ".</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Bước 4: dịch vụ kèm theo -->
                <div class="sz-card mb-4">
                    <p class="sz-step-label"><span class="step-num">4</span> Dịch vụ bổ sung <span class="text-muted fw-normal" style="font-size:13px;">(Tùy chọn)</span></p>
                    <c:choose>
                        <c:when test="${not empty serviceList}">
                            <c:forEach var="sv" items="${serviceList}">
                                <div class="service-item-row sz-service-row" data-price="${sv.price}">
                                    <div class="form-check d-flex align-items-center gap-2">
                                        <input class="form-check-input"
                                               type="checkbox"
                                               name="serviceId"
                                               value="${sv.serviceId}"
                                               id="svc${sv.serviceId}"
                                               style="width:18px; height:18px; cursor:pointer;"
                                               ${not empty selectedServiceQuantities[sv.serviceId] ? 'checked' : ''}>
                                        <label class="form-check-label fw-bold" for="svc${sv.serviceId}" style="cursor:pointer; font-size:14px;">
                                            <c:out value="${sv.serviceName}"/>
                                        </label>
                                    </div>
                                    <span class="sz-svc-price"><fmt:formatNumber value="${sv.price}" type="number" groupingUsed="true"/> đ</span>
                                    <input class="sz-qty"
                                           type="number"
                                           name="qty_${sv.serviceId}"
                                           value="${empty selectedServiceQuantities[sv.serviceId]
                                                    ? 1
                                                    : selectedServiceQuantities[sv.serviceId]}"
                                           min="1"
                                           max="20"
                                           ${empty selectedServiceQuantities[sv.serviceId] ? 'disabled' : ''}
                                           aria-label="Số lượng dịch vụ">
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted mb-0" style="font-size:13.5px;">Hiện chưa có dịch vụ kèm theo.</p>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Ghi chú -->
                <div class="sz-card mb-4">
                    <label class="form-label fw-bold" for="notes" style="font-size:14px; color:var(--ink);">
                        Ghi chú yêu cầu đặc biệt
                        <span class="text-muted fw-normal" style="font-size:13px;">(không bắt buộc)</span>
                    </label>

                    <textarea class="form-control"
                              id="notes"
                              name="notes"
                              maxlength="255"
                              rows="3"
                              placeholder="Ví dụ: Cần mượn thêm vợt thi đấu, chuẩn bị bóng sớm..."
                              style="border-radius:12px; padding:12px; font-size:14px;"><c:out value="${param.notes}"/></textarea>
                </div>

                <button class="btn-action-green booking-submit" type="submit" ${sessionScope.frozen ? 'disabled' : ''}>
                    Xác Nhận Đặt Sân Ngay →
                </button>
            </form>
        </div>

        <!-- ===== RIGHT COLUMN: STICKY SUMMARY BOX ===== -->
        <div>
            <div class="summary-card">
                <h2>Tóm Tắt Đặt Sân</h2>
                <div class="sum-row"><span>Sân thi đấu</span><strong id="szSumCourt">—</strong></div>
                <div class="sum-row"><span>Ngày chơi</span><strong id="szSumDate">—</strong></div>
                <div class="sum-row"><span>Khung giờ</span><strong id="szSumSlot">—</strong></div>
                <div class="sum-row"><span>Tiền thuê sân</span><strong id="szSumCourtPrice">0 đ</strong></div>
                <div class="sum-row"><span>Dịch vụ kèm theo</span><strong id="szSumService">0 đ</strong></div>
                <div class="sum-row total"><span>Tổng thanh toán</span><strong id="szSumTotal">0 đ</strong></div>
                
                <p class="text-muted small mt-3 mb-0" style="line-height:1.6;">
                    Booking ở trạng thái <span class="sz-badge sz-badge-pending">Chờ xác nhận</span> —
                    hóa đơn thanh toán sẽ được lập tại quầy khi bạn đến nhận sân thi đấu.
                </p>
            </div>
        </div>
    </div>
</div>

<script>
    (function () {
        var form = document.getElementById('szBookingForm');
        if (!form) return;
        var fmt = new Intl.NumberFormat('vi-VN');

        function money(n) {
            return fmt.format(n) + ' đ';
        }

        function updateTotal() {
            var courtPrice = parseFloat(form.getAttribute('data-court-price')) || 0;
            var svcTotal = 0;
            document.querySelectorAll('.sz-service-row').forEach(function (row) {
                var cb = row.querySelector('input[type="checkbox"]');
                var qty = row.querySelector('.sz-qty');
                if (cb && cb.checked) {
                    var q = Math.max(1, parseInt(qty.value, 10) || 1);
                    svcTotal += (parseFloat(row.getAttribute('data-price')) || 0) * q;
                }
            });
            document.getElementById('szSumCourtPrice').textContent = money(courtPrice);
            document.getElementById('szSumService').textContent = money(svcTotal);
            document.getElementById('szSumTotal').textContent = money(courtPrice + svcTotal);
        }

        function updateSlotSummary() {
            var checked = form.querySelector('.sz-slot-radio:checked');
            document.querySelectorAll('label.slot-radio-label').forEach(function (l) {
                l.classList.remove('is-selected');
            });
            if (checked) {
                checked.closest('.slot-radio-label').classList.add('is-selected');
                document.getElementById('szSumSlot').textContent =
                        checked.getAttribute('data-start') + ' – ' + checked.getAttribute('data-end');
            } else {
                document.getElementById('szSumSlot').textContent = '—';
            }
        }

        function updateCourtSummary() {
            var sel = document.querySelector('select[name="courtId"]');
            var dateInput = document.querySelector('input[name="date"]');
            if (sel && sel.selectedIndex >= 0) {
                document.getElementById('szSumCourt').textContent =
                        sel.options[sel.selectedIndex].textContent.split(' — ')[0].trim();
            }
            if (dateInput && dateInput.value) {
                var p = dateInput.value.split('-');
                document.getElementById('szSumDate').textContent = p[2] + '/' + p[1] + '/' + p[0];
            }
        }

        form.addEventListener('change', function (e) {
            var t = e.target;
            if (t.classList.contains('sz-slot-radio')) {
                updateSlotSummary();
            }
            if (t.type === 'checkbox') {
                var qty = t.closest('.sz-service-row').querySelector('.sz-qty');
                if (qty) qty.disabled = !t.checked;
            }
            updateTotal();
        });

        form.addEventListener('submit', function (e) {
            if (!form.querySelector('.sz-slot-radio:checked')) {
                e.preventDefault();
                alert('Vui lòng chọn một khung giờ trống.');
                return;
            }
            var btn = form.querySelector('button[type="submit"]');
            if (btn) {
                btn.disabled = true;
                btn.textContent = 'Đang xử lý…';
            }
        });

        updateCourtSummary();
        updateSlotSummary();
        updateTotal();
    })();
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
