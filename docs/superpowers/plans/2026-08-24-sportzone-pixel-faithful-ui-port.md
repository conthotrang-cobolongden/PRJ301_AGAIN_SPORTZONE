# SportZone Pixel-Faithful UI Port Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Port all page-specific designs in `design-system/sportzone/**` except `preview/**` into the production JSP application in one phase, replacing demo data with current EL/JSTL and locking unsupported functions as `Coming Soon`.

**Architecture:** Perform a contract-preserving presentation rebuild. Shared design tokens, responsive rules, and reusable components live in `app.css`; each JSP receives the matching design markup wrapped by a page namespace. Java, SQL, routes, and application configuration remain untouched.

**Tech Stack:** JSP 2.x, JSTL, Bootstrap 5.3, Bootstrap Icons, HTML5, CSS3, vanilla JavaScript, NetBeans Ant, Tomcat 9.

**Execution constraint:** One implementation phase, one consolidated verification round, one implementation commit. Execute inline; do not use sub-agents.

---

## Phase 1: Pixel-faithful port and consolidated verification

### Task 1: Freeze current UI contracts before markup changes

**Files:**
- Create: `SportZoneManagement/test/utils/UiRedesignContractTest.java`
- Read-only: `SportZoneManagement/src/java/controller/**`
- Read-only: `SportZoneManagement/src/java/filter/**`

- [ ] Record source assertions for every current form action, method, input name, hidden CSRF/ID field, request/session attribute, EL property, role/status/ownership condition, and functional DOM hook.
- [ ] Assert that production JSPs contain no demo inspector, demo switcher, demo autofill, or fake business rows.
- [ ] Assert that unsupported Admin sections retain disabled controls and the literal label `Coming Soon`.
- [ ] Add `UiRedesignContractTest.main(args)` to `AdminMvpRegressionBatchTest` without running it yet; it will execute only in the final consolidated round.

Contract groups to preserve:

```text
Auth: login(email,password,csrfToken), register(fullName,email,phone,password,confirmPassword,csrfToken)
Account: profile(fullName,email,phone,csrfToken), password(currentPassword,newPassword,confirmPassword,csrfToken)
Courts: courts(type), court(id,date)
Booking create: courtId,date,slotId,serviceId,qty_<id>,notes,csrfToken; szBookingForm and szSum* hooks
Booking detail: bookingId/cancelReason/operation/notice fields and existing canCancel/payment ownership conditions
Invoice: bookingId,operation,csrfToken and existing User/Admin ownership navigation
Admin courts: operation,weekStart,courtId,price,status,csrfToken
Admin bookings: status,id,operation,bookingId,csrfToken and canCheckIn/ownership conditions
Shared session: account,roleName,frozen,csrfToken
```

### Task 2: Port the shared visual foundation exactly

**Files:**
- Modify: `SportZoneManagement/web/assets/css/app.css`
- Modify: `SportZoneManagement/web/WEB-INF/views/layout/header.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/layout/footer.jsp`

- [ ] Copy the approved green/gold tokens, Be Vietnam Pro/Montserrat typography, reset, buttons, badges, cards, forms, tables, shadows, radii, transitions, focus states, and responsive rules from the page-specific designs into `app.css`.
- [ ] Namespace page-specific rules under `.sz-page-home`, `.sz-page-auth`, `.sz-page-court`, `.sz-page-booking`, `.sz-page-invoice`, `.sz-page-account`, `.sz-page-error`, and `.sz-page-admin` to prevent collisions without altering their visual values.
- [ ] Port the exact public header/mobile menu/footer appearance from the design while retaining all current role-based JSTL links and the frozen-account banner.
- [ ] Preserve Bootstrap includes, application CSS URL, context-relative navigation, and the existing reveal behavior; remove only demo-only inspector/switcher behavior.

### Task 3: Port public and authentication pages

**Files:**
- Modify: `SportZoneManagement/web/WEB-INF/views/home/index.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/auth/login.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/auth/register.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/court/list.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/court/detail.jsp`

- [ ] Copy `home/sportzone-home-demo.html` sections in their original order and structure. Bind current counts/session visibility; where the Home backend does not provide the ten court rows, retain the designed catalog section as disabled `Coming Soon` cards without fake court data.
- [ ] Copy both auth modes from `login/sportzone-auth-demo.html` into the separate login/register JSPs, retaining their exact forms, errors, CSRF, field values, links, responsiveness, and animation; omit only demo account autofill and inspector controls.
- [ ] Copy list/detail views from `court/sportzone-court-demo.html`; turn repeated court/date/slot markup into the current `courtList`, `dateOptions`, and `slotList` loops while preserving all status branches and links.

Dynamic conversion pattern:

```jsp
<c:forEach var="c" items="${courtList}">
    <!-- exact design card structure -->
    <h2 class="court-title"><c:out value="${c.courtName}"/></h2>
    <fmt:formatNumber value="${c.pricePerHour}" groupingUsed="true"/> đ
</c:forEach>
```

### Task 4: Port booking, invoice, and account pages

**Files:**
- Modify: `SportZoneManagement/web/WEB-INF/views/booking/create.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/booking/list.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/booking/detail.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/invoice/list.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/invoice/detail.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/invoice/payment.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/account/profile.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/account/password.jsp`

- [ ] Copy the three booking views exactly from `booking/sportzone-booking-demo.html`, preserving all existing booking loops, status/payment mappings, cancel/payment actions, services, errors, empty states, and realtime summary hooks.
- [ ] Copy invoice list/detail structure from `invoice/sportzone-invoice-demo.html`; adapt the payment page to the same paper/payment visual language while retaining the current QR asset and pay/cancel POST contracts.
- [ ] Copy profile/password modes from `account/sportzone-account-demo.html`, retaining membership, frozen, success/error, validation, CSRF, and password-strength hooks. Keep the unsupported preferences panel visible, disabled, and labelled `Coming Soon`.

### Task 5: Port Admin and error states

**Files:**
- Modify: `SportZoneManagement/web/WEB-INF/views/admin/dashboard.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/admin/courts.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/admin/bookings.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/error/403.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/error/404.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/error/500.jsp`
- Modify: `SportZoneManagement/web/WEB-INF/views/error/account-locked.jsp`

- [ ] Copy the Admin sidebar, topbar, responsive shell, KPI cards, tables, court grid, booking views, and animation from `admin/sportzone-admin-demo.html`.
- [ ] Bind the four live dashboard values, current court forms, weekly generator, booking filters/detail/check-in, and walk-in links to the existing JSP contracts.
- [ ] Retain unsupported Users, standalone Timeslot CRUD, and Admin Invoice views in the designed navigation/shell; disable controls/links and attach `Coming Soon` without fake rows or fake totals.
- [ ] Copy 403/404/500 presentation from `error/sportzone-error-demo.html`; adapt account-locked to the same visual system while preserving its standalone page, account name, and logout URL.

### Task 6: Run the single consolidated verification round

**Files:**
- Modify: `rules/LOG-LAM-VIEC.md`
- Modify: `docs/tien-do.md`

- [ ] Run static checks for forbidden preview/demo hooks, JSP contracts, `Coming Soon`, balanced JSP/JSTL usage, and `git diff --check`.
- [ ] Run one compile/regression/build batch from `SportZoneManagement`:

```powershell
$ant='C:\Program Files\Apache NetBeans\extide\ant\bin\ant.bat'
$java='C:\Program Files\Java\jdk1.8.0_172\bin\java.exe'
& $ant clean compile-test
$cp='build\test\classes;build\web\WEB-INF\classes;web\WEB-INF\lib\*;E:\0_FPTU\SEMESTER_4_SUM26\PRJ\apache-tomcat-9.0.118\lib\servlet-api.jar'
& $java -cp $cp utils.AdminMvpRegressionBatchTest
& $ant dist
```

Expected: both Ant commands report `BUILD SUCCESSFUL`, regression batch reports `PASSED`, and `dist/SportZoneManagement.war` exists.

- [ ] Smoke Tomcat once across public, User, Frozen User, and Admin routes; verify primary GET/POST flows do not return 500 and unauthorized Admin access remains 403.
- [ ] Inspect representative pages at 375px, 768px, and 1200px using the in-app browser; compare directly against the corresponding page-specific design files, excluding `preview/**`.
- [ ] Fix defects inside the same verification round and rerun the same batch only if required.
- [ ] Record only actual results in the work log/progress docs.
- [ ] Commit the one-phase implementation:

```powershell
git add SportZoneManagement rules/LOG-LAM-VIEC.md docs/tien-do.md
git commit -m "feat: port final SportZone UI designs"
```
