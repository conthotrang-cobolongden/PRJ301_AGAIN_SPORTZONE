<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Quản lý sân — SportZone" scope="request"/>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<section class="container sz-section">
    <div class="sz-pagehead"><div><p class="sz-kicker sz-kicker-dark mb-1">Admin MVP</p><h1>Quản lý sân và lịch tuần</h1></div></div>
    <c:if test="${not empty success}"><div class="alert alert-success"><c:out value="${success}"/></div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>

    <div class="sz-card mb-4">
        <h2 class="h5">Sinh slot theo tuần cho toàn bộ 10 sân</h2>
        <form method="post" action="${ctx}/MainController?action=adminCourts" class="row g-3 align-items-end">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
            <input type="hidden" name="operation" value="generateWeek"/>
            <div class="col-sm-6"><label class="form-label" for="weekStart">Thứ Hai bắt đầu tuần</label><input class="form-control" id="weekStart" name="weekStart" type="date" value="${defaultWeek}" required/></div>
            <div class="col-sm-auto"><button class="btn btn-sz" type="submit">Sinh slot theo tuần</button></div>
        </form>
    </div>

    <div class="table-responsive sz-card p-0">
        <table class="table align-middle mb-0"><thead><tr><th>Sân</th><th>Loại</th><th>Giá/giờ</th><th>Trạng thái</th><th></th></tr></thead><tbody>
        <c:forEach var="court" items="${courtList}">
            <tr>
                <td><strong><c:out value="${court.courtName}"/></strong></td>
                <td><c:out value="${court.typeName}"/></td>
                <td><input form="courtForm${court.courtId}" class="form-control" name="price" type="number" min="0" step="1000" value="${court.pricePerHour}" required/></td>
                <td><select form="courtForm${court.courtId}" class="form-select" name="status">
                    <option value="Available" ${court.status == 'Available' ? 'selected' : ''}>Available</option>
                    <option value="Maintenance" ${court.status == 'Maintenance' ? 'selected' : ''}>Maintenance</option>
                    <option value="Unavailable" ${court.status == 'Unavailable' ? 'selected' : ''}>Unavailable</option>
                </select></td>
                <td><form id="courtForm${court.courtId}" method="post" action="${ctx}/MainController?action=adminCourts"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/><input type="hidden" name="operation" value="updateCourt"/><input type="hidden" name="courtId" value="${court.courtId}"/><button class="btn btn-sm btn-sz-outline" type="submit">Lưu</button></form></td>
            </tr>
        </c:forEach>
        </tbody></table>
    </div>
</section>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
