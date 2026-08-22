# Phase 2 Auth & Authorization Design

**Status:** Approved baseline, consolidated from `docs/phase-2-auth.md`

**Scope:** A1, A2, U1, U2, U3 — login, registration, logout, authenticated-route protection, and Admin authorization.

## Context

Phase 1 is complete on `feature/foundation`. The current source already provides the shared `Account` model, password hashing, validation, CSRF support, session helpers, centralized JSP paths, layout, and auth JSPs. Phase 2 must add the missing DAO, service, controllers, filters, mappings, and focused tests without changing the established Bootstrap/JSP presentation.

The old Phase 2 implementation in Git commit `33ae228` is reference material only. It uses the obsolete `com.sportzone.*` package layout and APIs that no longer exactly match the current foundation.

## Considered Approaches

### 1. Vertical TDD slices — selected

Build and verify the database contract, service rules, controller behavior, filters, deployment descriptor, and end-to-end smoke flow in small commits. Existing Phase 1 helpers remain the shared boundary.

This requires a little test seam work, such as constructor injection for `AuthService`, but gives failures close to their source and avoids a large unverified integration step.

### 2. Complete every backend layer, then test

Create DAO, service, all controllers, and filters first, followed by one test pass. This is quicker to type but makes a failure harder to localize and conflicts with the repository's requirement to review small changes.

### 3. Restore the historical Phase 2 code and rename packages

Recover the implementation from `33ae228`, mechanically replace packages, then patch compilation errors. This is rejected because the historical code contains stale constant names, old time types, methods belonging to Phase 4, and mappings that do not protect the exact `/booking` and `/invoice` detail URLs.

## Architecture

The request flow remains classic Servlet/JSP MVC:

1. `EncodingFilter` applies UTF-8.
2. `AuthFilter` protects authenticated routes and stores a local GET target in `redirectAfterLogin`.
3. `RoleFilter` rejects non-Admin access to `/admin/*` with HTTP 403.
4. Auth servlets validate CSRF before every POST and delegate business rules to `AuthService`.
5. `AuthService` normalizes input, validates registration, hashes passwords, and talks to `AccountDAO`.
6. `AccountDAO` is the only Phase 2 class containing SQL. It maps `Accounts JOIN Roles` into `Account` objects.
7. `SessionUtil` stores exactly `account`, `roleName`, and `frozen`, removes the password hash, and ensures a CSRF token exists.

## Component Boundaries

### `AccountDAO`

Owns `findByEmail`, `findById`, `existsByEmail`, `existsByPhone`, `insert`, and `updateLastLogin`. All queries use `PreparedStatement`; all account reads reuse one `mapAccount(ResultSet)` function. Connections are obtained from `DBUtils` and closed with try-with-resources.

### `AuthService`

Owns login and registration rules. Login returns `null` for missing email, missing password, unknown email, or wrong password so callers expose one generic error. Registration returns an immutable result containing either the created account or ordered field errors. It always assigns role ID 2, `User`, and status `Active`.

### Controllers

- `LoginServlet`: renders the form, rejects invalid CSRF, authenticates, stores the safe account, consumes `redirectAfterLogin`, and otherwise redirects both roles to `/home` until P8 exists.
- `RegisterServlet`: renders the form, rejects invalid CSRF, preserves only non-password fields after validation errors, catches SQLState `23000`, automatically logs in the new account, and redirects to `/home`.
- `LogoutServlet`: invalidates the entire existing session and redirects to `/home`.

### Filters

- `AuthFilter`: authenticated requests continue. Unauthenticated GET requests save servlet path plus query string; unauthenticated non-GET requests do not save a replay target. All unauthenticated requests redirect to `/login`.
- `RoleFilter`: only session role `Admin` continues; every other request returns 403.

## Data and Security Rules

- Session contains only `account`, `roleName`, `frozen`, and the existing CSRF token. `Account.passwordHash` is cleared before storage.
- Every Phase 2 POST validates the session CSRF token before reading credentials or changing state.
- Login always reports `Email hoặc mật khẩu không đúng.` for invalid credentials.
- Registration normalizes email to lowercase and removes spaces/hyphens from phone numbers before validation and persistence.
- Registration never echoes password or confirmation values.
- Unique pre-checks provide field-specific UX; the database constraints and SQLState `23000` catch races.
- Frozen accounts may log in. They receive `frozen=true`; booking/payment restrictions remain Phase 5/6 responsibilities.
- Redirect-after-login values are created only from server request paths inside the current application and are removed immediately after consumption.

## URL and View Contract

- Public: `GET/POST /login`, `GET/POST /register`, `GET /logout`.
- Authenticated: `/profile`, `/password`, `/booking`, `/booking/*`, `/bookings`, `/invoice`, `/invoice/*`, `/invoices`, `/admin/*`.
- Admin only: `/admin/*`.
- Views remain `/WEB-INF/views/auth/login.jsp` and `/WEB-INF/views/auth/register.jsp`.
- Login error attribute is standardized as `errorMessage`; registration errors remain `fieldErrors` with keys `fullName`, `email`, `phone`, `password`, and `confirmPassword`.

## Error Handling

- Validation errors forward to the same form with HTTP 200 and precise messages.
- Invalid or absent CSRF returns 403 and performs no authentication or insert.
- SQLState `23000` becomes a friendly duplicate-account field error.
- Other SQL failures are wrapped in `ServletException` and handled by the configured 500 page.
- Unauthorized Admin access returns 403 without redirect loops.

## Testing Strategy

- Main-method contract tests follow the repository's existing lightweight test style.
- DAO integration tests run against seeded `SportZoneDB` and clean up the uniquely named account they create.
- Service tests use a fake `AccountDAO` through constructor injection to cover normalization, validation, generic login failure, password hashing, and fixed role/status values.
- Servlet/filter tests use JDK dynamic proxies for servlet interfaces and injected fakes where needed.
- A deployment-descriptor/JSP contract test verifies servlet/filter order, URL coverage, CSRF fields, error attribute names, and absence of password echo.
- Final verification builds a clean WAR and smoke-tests all Phase 2 Definition of Done cases on Tomcat 9.

## Out of Scope

- Profile editing and password changes (Phase 4).
- Blocking Frozen users from booking or transfer notices (Phase 5/6).
- Admin dashboard implementation and Admin redirect to `/admin` (Phase 8).
- Remember-me, email reset, brute-force lockout, BCrypt migration, and REST/JWT APIs.

## Acceptance Criteria

1. All four seed accounts log in; the Frozen account gets the warning state.
2. Unknown email and wrong password produce the same message.
3. Registration validates every field, reports duplicate email/phone, creates an Active User, and logs it in.
4. Unauthenticated protected GET requests return to their local target after login.
5. User access to `/admin` is rejected with 403.
6. Logout invalidates the session.
7. Every Phase 2 POST with absent or invalid CSRF is rejected with 403.
8. Ant clean/build and all targeted contract tests pass; Tomcat smoke tests confirm the deployed behavior.
