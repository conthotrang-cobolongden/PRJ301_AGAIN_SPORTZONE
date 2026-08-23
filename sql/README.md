# SportZoneDB — Database setup

SQL Server database for **PRJ301 Sports Complex** (Tennis + Pickleball).

## Files

| File | Description |
|------|-------------|
| `01_SportZoneDB_Schema.sql` | Drop/create DB + all tables, indexes, constraints |
| `02_SportZoneDB_Seed.sql` | Master data + sample bookings/invoices |
| `00_SportZoneDB_Init_All.sql` | Optional SQLCMD runner (`:r`) — needs SQLCMD Mode |
| `03_SportZoneDB_AdminMvp_Migration.sql` | Non-destructive upgrade for Admin MVP check-in lifecycle |

## How to run

### Option A — SSMS / Azure Data Studio

1. Connect to local SQL Server (`localhost` or `.\SQLEXPRESS`)
2. Execute **`01_SportZoneDB_Schema.sql`** fully
3. Execute **`02_SportZoneDB_Seed.sql`** fully

### Option B — sqlcmd

```bat
sqlcmd -S localhost -U SA -P 12345 -C -I -i 01_SportZoneDB_Schema.sql
sqlcmd -S localhost -U SA -P 12345 -C -I -i 02_SportZoneDB_Seed.sql
```

`-I` = `QUOTED_IDENTIFIER ON` (required for filtered unique index on Bookings).

> Script `01` **drops** database `SportZoneDB` if it exists. Do not run on production data.

For a database already created by Phase 1–6, do not rerun schema/seed. Execute
`03_SportZoneDB_AdminMvp_Migration.sql` once to add `Bookings.CheckInAt`, the
`CheckedIn` status and the updated active-slot unique index.

**Verified:** schema + seed run successfully on local SQL Server (10 courts, 560 time slots, sample bookings/invoices).

## Demo accounts

| Role | Email | Password | Hash algorithm |
|------|-------|----------|----------------|
| Admin | `admin@sportzone.local` | `Admin@123` | SHA-256 (hex) |
| User | `user1@sportzone.local` | `User@123` | SHA-256 (hex) |
| User | `user2@sportzone.local` | `User@123` | SHA-256 (hex) |
| User (Frozen) | `user3@sportzone.local` | `User@123` | SHA-256 (hex) |

In Java login, hash the input password with SHA-256 and compare to `PasswordHash`  
(or switch both app + seed to BCrypt later — column is `NVARCHAR(255)`).

## Tables (9)

| Table | Purpose | CRUD focus |
|-------|---------|------------|
| `Roles` | Admin / User | seed |
| `Accounts` | Login accounts | Yes |
| `CourtTypes` | Tennis, Pickleball | Read (fixed) |
| `Courts` | 5 + 5 courts | Read / Update status & price |
| `TimeSlots` | Per court, per date hour slots | Yes |
| `Services` | Add-ons (racket, coach…) | Yes |
| `Bookings` | Court reservations | Yes |
| `BookingServices` | Booking line items | with Booking |
| `Invoices` | Invoice + payment status/method (optional) | Yes |

Cardinality (ERD v3): [`docs/erd-file/erd.md`](../docs/erd-file/erd.md).

| Relationship | Cardinality |
|--------------|-------------|
| Roles → Accounts | 1 : 0..N |
| CourtTypes → Courts | 1 : 1..N (2 types fixed; each has courts) |
| Courts → TimeSlots | 1 : 0..N |
| Accounts → Bookings | 1 : 0..N |
| TimeSlots → Bookings | 1 : 0..N (active ≈ 0..1) |
| Bookings → Invoices | **1 : 0..1** (created when collection starts) |
| Bookings → BookingServices | 1 : 0..N |
| Services → BookingServices | 1 : 0..N |

Không có FK trực tiếp `Courts → Bookings`; sân của booking được suy ra qua `Bookings.TimeSlotID → TimeSlots.CourtID`.

## Fixed inventory

- **Court types:** Tennis, Pickleball only — no create/delete type  
- **Courts:** Tennis Court 1–5, Pickleball Court 1–5  
- Court 5 (Tennis) seeded as **Maintenance** (slots Closed)

## Invoice rule

A booking may have **no invoice**. `Invoices.BookingID` is UNIQUE (at most one).  
`Unpaid` / `Paid` / `Cancelled` live on `Invoices.PaymentStatus`.  
If the payment deadline passes and an invoice exists, it stays `Unpaid`.  
If there is no invoice, there is no Unpaid row — handle the booking (`Cancelled`).

## Connection (Java)

```text
jdbc:sqlserver://localhost:1433;databaseName=SportZoneDB
```

Update `DBUtils` with your SA (or SQL user) credentials.
