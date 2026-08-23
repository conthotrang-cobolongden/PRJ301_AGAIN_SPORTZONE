# SportZone Pixel-Faithful UI Port Design

**Date:** 2026-08-24  
**Scope:** One implementation phase, one consolidated verification round.

## Objective

Treat every page-specific HTML file under `design-system/sportzone/**`, except `design-system/sportzone/preview/**`, as the final visual source of truth. Port that interface into the production JSP application without redesigning, simplifying, reordering, or removing visual sections.

The only intended difference between demo and production is data binding: hard-coded demo values become existing EL/JSTL expressions, request/session attributes, query parameters, and form values.

## Non-negotiable rules

- Preserve layout, sections, component structure, colors, fonts, spacing, sizing, animation, responsive behavior, header, footer, cards, forms, tables, and content placement from the matching design HTML.
- Do not reinterpret, optimize, or take inspiration from the design. Copy it faithfully.
- Completely ignore `design-system/sportzone/preview/**`; it is a reference compilation and is not a design source for this implementation.
- Preserve all backend contracts: routes, HTTP methods, form actions, input names, parameter names, hidden fields, CSRF, request/session attributes, EL bean properties, JSTL business conditions, authorization, and JavaScript hooks.
- Do not modify Java, SQL, `web.xml`, dependencies, build configuration, or environment configuration.
- Never ship demo business data as production data.
- If a designed function has no backend support, keep its visual section, disable every interaction, and label it `Coming Soon`.

## Template-to-JSP mapping

| Design source | Production targets |
|---|---|
| `home/sportzone-home-demo.html` | `home/index.jsp`, shared header/footer |
| `login/sportzone-auth-demo.html` | `auth/login.jsp`, `auth/register.jsp` |
| `court/sportzone-court-demo.html` | `court/list.jsp`, `court/detail.jsp` |
| `booking/sportzone-booking-demo.html` | `booking/create.jsp`, `booking/list.jsp`, `booking/detail.jsp` |
| `invoice/sportzone-invoice-demo.html` | `invoice/list.jsp`, `invoice/detail.jsp`, `invoice/payment.jsp` |
| `account/sportzone-account-demo.html` | `account/profile.jsp`, `account/password.jsp`, `error/account-locked.jsp` |
| `error/sportzone-error-demo.html` | `error/403.jsp`, `error/404.jsp`, `error/500.jsp` |
| `admin/sportzone-admin-demo.html` | `admin/dashboard.jsp`, `admin/courts.jsp`, `admin/bookings.jsp` |

## Dynamic data policy

- Repeated demo cards/rows become `c:forEach` loops while retaining the card/row markup.
- Demo fields become matching current bean properties and are escaped with `c:out` where appropriate.
- Status presentation keeps existing backend status values and maps only the displayed badge/label.
- Empty, error, success, frozen, ownership, and role-dependent states remain controlled by current JSTL conditions.
- Sections without available request data stay visually present with neutral placeholders, disabled controls, and `Coming Soon`; they must not pretend to be live data.

## UI architecture

- Consolidate the design tokens and shared components in `web/assets/css/app.css`.
- Use page-root classes to prevent the large template styles from leaking between auth, public, account, invoice, and admin screens.
- Keep shared production navigation in `layout/header.jsp` and `layout/footer.jsp`, but reproduce the design appearance and responsive behavior.
- Page-specific scripts may be placed under `web/assets/js/**` or retained in the relevant JSP when they depend on page EL values. Existing functional DOM IDs and `data-*` hooks remain unchanged.
- Admin pages reproduce the designed sidebar/control-center shell. Unsupported Admin menu sections remain visible, disabled, and marked `Coming Soon`.

## Contract snapshot requirements

Before editing each target JSP, record and preserve:

- GET/POST route and form action.
- Query and input parameter names.
- CSRF and record-ID hidden fields.
- Request/session attributes and EL properties.
- JSTL role, status, ownership, error, success, and empty-state conditions.
- JavaScript IDs, selectors, and `data-*` hooks.

Static contract comparison after the port must show no unapproved contract changes.

## Verification

Run one consolidated verification round after all pages are ported:

1. Static UI contract checks across all JSP files.
2. Compile/test batch and WAR build once.
3. Tomcat smoke for public, User, Frozen User, and Admin routes.
4. Visual/responsive checks at approximately 375px, 768px, and 1200px on representative auth, public, data-table/form, and Admin pages.
5. Confirm no JSP HTTP 500, missing primary action, broken navigation, horizontal overflow, or unauthorized action exposure.

If verification reveals a defect, fix it and rerun the same consolidated round; this remains the single verification phase.

## Success criteria

- Production JSP screens visually match their source design files.
- Live backend data replaces demo data wherever the backend supplies it.
- Unsupported features remain visible but safely disabled as `Coming Soon`.
- Existing application behavior and security contracts remain intact.
- The application builds and the consolidated runtime/visual smoke passes.
