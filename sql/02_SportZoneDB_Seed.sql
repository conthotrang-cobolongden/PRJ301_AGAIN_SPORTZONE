-- =============================================================================
-- SportZoneDB — Seed data
-- Run AFTER: 01_SportZoneDB_Schema.sql
-- =============================================================================
-- Demo accounts (password hashed with SHA-256 hex, UTF-8, no salt — demo only):
--   Admin : admin@sportzone.local  / Admin@123
--   User  : user1@sportzone.local  / User@123
--           user2@sportzone.local  / User@123
-- App layer should hash the same way (or migrate to BCrypt later).
-- =============================================================================

USE SportZoneDB;
GO

SET NOCOUNT ON;

-- SCHEMA_CONTRACT_BOOKINGS_V3:
-- Fail before seeding when this script is accidentally run against the legacy DB.
IF OBJECT_ID(N'dbo.Bookings', N'U') IS NULL
    THROW 51010, N'Canonical schema is missing. Run 01_SportZoneDB_Schema.sql first.', 1;
IF COL_LENGTH(N'dbo.Bookings', N'TimeSlotID') IS NULL
    THROW 51011, N'Legacy Bookings schema detected: TimeSlotID is missing.', 1;
IF COL_LENGTH(N'dbo.Bookings', N'CourtID') IS NOT NULL
    THROW 51012, N'Legacy Bookings schema detected: remove direct CourtID by rerunning 01.', 1;
GO

-- -----------------------------------------------------------------------------
-- 1. Roles
-- -----------------------------------------------------------------------------
SET IDENTITY_INSERT Roles ON;
INSERT INTO Roles (RoleID, RoleName) VALUES
    (1, N'Admin'),
    (2, N'User');
SET IDENTITY_INSERT Roles OFF;
GO

-- -----------------------------------------------------------------------------
-- 2. Accounts
-- SHA-256('Admin@123') = e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7
-- SHA-256('User@123')  = 3e7c19576488862816f13b512cacf3e4ba97dd97243ea0bd6a2ad1642d86ba72
-- -----------------------------------------------------------------------------
SET IDENTITY_INSERT Accounts ON;
INSERT INTO Accounts (AccountID, RoleID, Email, Phone, PasswordHash, FullName, Status, CreatedAt) VALUES
    (1, 1, N'admin@sportzone.local',  N'0901000001',
     N'e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7',
     N'System Admin', N'Active', GETDATE()),
    (2, 2, N'user1@sportzone.local',  N'0902000001',
     N'3e7c19576488862816f13b512cacf3e4ba97dd97243ea0bd6a2ad1642d86ba72',
     N'Nguyen Van A', N'Active', GETDATE()),
    (3, 2, N'user2@sportzone.local',  N'0902000002',
     N'3e7c19576488862816f13b512cacf3e4ba97dd97243ea0bd6a2ad1642d86ba72',
     N'Tran Thi B', N'Active', GETDATE()),
    (4, 2, N'user3@sportzone.local',  N'0902000003',
     N'3e7c19576488862816f13b512cacf3e4ba97dd97243ea0bd6a2ad1642d86ba72',
     N'Le Van C', N'Frozen', GETDATE());
SET IDENTITY_INSERT Accounts OFF;
GO

-- -----------------------------------------------------------------------------
-- 3. Court types (fixed)
-- -----------------------------------------------------------------------------
SET IDENTITY_INSERT CourtTypes ON;
INSERT INTO CourtTypes (CourtTypeID, TypeName, Description, IsActive) VALUES
    (1, N'Tennis',     N'Outdoor / indoor tennis courts', 1),
    (2, N'Pickleball', N'Pickleball courts for singles/doubles', 1);
SET IDENTITY_INSERT CourtTypes OFF;
GO

-- -----------------------------------------------------------------------------
-- 4. Courts — 5 Tennis + 5 Pickleball (hardcoded inventory)
-- -----------------------------------------------------------------------------
SET IDENTITY_INSERT Courts ON;
INSERT INTO Courts (CourtID, CourtTypeID, CourtName, Location, PricePerHour, Description, ImageURL, Status, IsActive) VALUES
    -- Tennis @ 150,000 VND/hour
    (1,  1, N'Tennis Court 1', N'Zone A - Court 1', 150000, N'Standard hard court', NULL, N'Available', 1),
    (2,  1, N'Tennis Court 2', N'Zone A - Court 2', 150000, N'Standard hard court', NULL, N'Available', 1),
    (3,  1, N'Tennis Court 3', N'Zone A - Court 3', 150000, N'Standard hard court', NULL, N'Available', 1),
    (4,  1, N'Tennis Court 4', N'Zone A - Court 4', 180000, N'Premium court with lights', NULL, N'Available', 1),
    (5,  1, N'Tennis Court 5', N'Zone A - Court 5', 150000, N'Standard hard court', NULL, N'Maintenance', 1),
    -- Pickleball @ 120,000 VND/hour
    (6,  2, N'Pickleball Court 1', N'Zone B - Court 1', 120000, N'Open court', NULL, N'Available', 1),
    (7,  2, N'Pickleball Court 2', N'Zone B - Court 2', 120000, N'Open court', NULL, N'Available', 1),
    (8,  2, N'Pickleball Court 3', N'Zone B - Court 3', 120000, N'Open court', NULL, N'Available', 1),
    (9,  2, N'Pickleball Court 4', N'Zone B - Court 4', 140000, N'Premium court with lights', NULL, N'Available', 1),
    (10, 2, N'Pickleball Court 5', N'Zone B - Court 5', 120000, N'Open court', NULL, N'Available', 1);
SET IDENTITY_INSERT Courts OFF;
GO

-- -----------------------------------------------------------------------------
-- 5. Services (add-ons)
-- -----------------------------------------------------------------------------
SET IDENTITY_INSERT Services ON;
INSERT INTO Services (ServiceID, ServiceName, Description, Price, DurationMinutes, IsActive) VALUES
    (1, N'Tennis Racket Rental',      N'Rent one tennis racket',           50000,  60, 1),
    (2, N'Pickleball Paddle Rental',  N'Rent one pickleball paddle',       40000,  60, 1),
    (3, N'Ball Tube / Pack',          N'Tube of balls for the session',    30000,  60, 1),
    (4, N'Coach 1 Hour',              N'Personal coach for 1 hour',       300000,  60, 1),
    (5, N'Water Bottle',              N'Bottled water',                    15000,   1, 1);
SET IDENTITY_INSERT Services OFF;
GO

-- -----------------------------------------------------------------------------
-- 6. Time slots — next 7 days, all active courts, fixed hour templates
-- Hours: 06-07, 07-08, 08-09, 16-17, 17-18, 18-19, 19-20, 20-21
-- -----------------------------------------------------------------------------
;WITH Days AS (
    SELECT CAST(CAST(GETDATE() AS DATE) AS DATE) AS SlotDate, 0 AS DayOffset
    UNION ALL
    SELECT DATEADD(DAY, DayOffset + 1, CAST(CAST(GETDATE() AS DATE) AS DATE)), DayOffset + 1
    FROM Days
    WHERE DayOffset < 6
),
Hours AS (
    SELECT CAST(N'06:00:00' AS TIME(0)) AS StartTime, CAST(N'07:00:00' AS TIME(0)) AS EndTime
    UNION ALL SELECT CAST(N'07:00:00' AS TIME(0)), CAST(N'08:00:00' AS TIME(0))
    UNION ALL SELECT CAST(N'08:00:00' AS TIME(0)), CAST(N'09:00:00' AS TIME(0))
    UNION ALL SELECT CAST(N'16:00:00' AS TIME(0)), CAST(N'17:00:00' AS TIME(0))
    UNION ALL SELECT CAST(N'17:00:00' AS TIME(0)), CAST(N'18:00:00' AS TIME(0))
    UNION ALL SELECT CAST(N'18:00:00' AS TIME(0)), CAST(N'19:00:00' AS TIME(0))
    UNION ALL SELECT CAST(N'19:00:00' AS TIME(0)), CAST(N'20:00:00' AS TIME(0))
    UNION ALL SELECT CAST(N'20:00:00' AS TIME(0)), CAST(N'21:00:00' AS TIME(0))
)
INSERT INTO TimeSlots (CourtID, SlotDate, StartTime, EndTime, Status)
SELECT
    c.CourtID,
    d.SlotDate,
    h.StartTime,
    h.EndTime,
    CASE
        WHEN c.Status = N'Maintenance' THEN N'Closed'
        WHEN c.Status = N'Unavailable' THEN N'Closed'
        ELSE N'Available'
    END
FROM Courts c
CROSS JOIN Days d
CROSS JOIN Hours h
WHERE c.IsActive = 1
OPTION (MAXRECURSION 100);
GO

-- -----------------------------------------------------------------------------
-- 7. Sample bookings + booking services + invoices
-- Bookings : Invoices = 1 : 0..1 (invoice is optional). Samples below include invoices
-- so payment screens have data; a booking without an invoice is still valid.
-- -----------------------------------------------------------------------------
DECLARE @Slot1 INT, @Slot2 INT, @Slot3 INT;
DECLARE @Court1 INT = 1;  -- Tennis Court 1
DECLARE @Court6 INT = 6;  -- Pickleball Court 1
DECLARE @PriceTennis DECIMAL(18,0) = 150000;
DECLARE @PricePickle DECIMAL(18,0) = 120000;

-- Pick first available slots for today/tomorrow on court 1 and 6
SELECT TOP 1 @Slot1 = TimeSlotID
FROM TimeSlots
WHERE CourtID = @Court1 AND Status = N'Available' AND SlotDate >= CAST(GETDATE() AS DATE)
ORDER BY SlotDate, StartTime;

SELECT TOP 1 @Slot2 = TimeSlotID
FROM TimeSlots
WHERE CourtID = @Court6 AND Status = N'Available' AND SlotDate >= CAST(GETDATE() AS DATE)
ORDER BY SlotDate, StartTime;

SELECT TOP 1 @Slot3 = TimeSlotID
FROM TimeSlots
WHERE CourtID = @Court1 AND Status = N'Available' AND TimeSlotID <> ISNULL(@Slot1, 0)
  AND SlotDate >= CAST(GETDATE() AS DATE)
ORDER BY SlotDate, StartTime;

-- Booking 1: User1, Tennis, Pending + Unpaid invoice + racket
IF @Slot1 IS NOT NULL
BEGIN
    DECLARE @Booking1 INT;
    DECLARE @SvcRacket DECIMAL(18,0) = 50000;

    INSERT INTO Bookings (AccountID, TimeSlotID, CourtPriceAtOrder, ServiceTotalAtOrder, TotalAmount, Status, Notes)
    VALUES (2, @Slot1, @PriceTennis, @SvcRacket, @PriceTennis + @SvcRacket, N'Pending', N'Sample pending booking');

    SET @Booking1 = SCOPE_IDENTITY();

    INSERT INTO BookingServices (BookingID, ServiceID, Quantity, PriceAtOrder)
    VALUES (@Booking1, 1, 1, @SvcRacket);

    INSERT INTO Invoices (BookingID, InvoiceCode, SubTotal, DiscountAmount, FinalAmount, PaymentStatus, PaymentMethod, Note)
    VALUES (
        @Booking1,
        N'INV-' + RIGHT(N'000000' + CAST(@Booking1 AS NVARCHAR(10)), 6),
        @PriceTennis + @SvcRacket,
        0,
        @PriceTennis + @SvcRacket,
        N'Unpaid',
        NULL,
        N'Awaiting payment'
    );

    UPDATE TimeSlots SET Status = N'Booked' WHERE TimeSlotID = @Slot1;
END

-- Booking 2: User2, Pickleball, Confirmed + Paid (Cash)
IF @Slot2 IS NOT NULL
BEGIN
    DECLARE @Booking2 INT;
    DECLARE @SvcPaddle DECIMAL(18,0) = 40000;

    INSERT INTO Bookings (AccountID, TimeSlotID, CourtPriceAtOrder, ServiceTotalAtOrder, TotalAmount, Status, Notes)
    VALUES (3, @Slot2, @PricePickle, @SvcPaddle, @PricePickle + @SvcPaddle, N'Confirmed', N'Sample paid booking');

    SET @Booking2 = SCOPE_IDENTITY();

    INSERT INTO BookingServices (BookingID, ServiceID, Quantity, PriceAtOrder)
    VALUES (@Booking2, 2, 1, @SvcPaddle);

    INSERT INTO Invoices (BookingID, InvoiceCode, SubTotal, DiscountAmount, FinalAmount, PaymentStatus, PaymentMethod, PaidAt, Note)
    VALUES (
        @Booking2,
        N'INV-' + RIGHT(N'000000' + CAST(@Booking2 AS NVARCHAR(10)), 6),
        @PricePickle + @SvcPaddle,
        0,
        @PricePickle + @SvcPaddle,
        N'Paid',
        N'Cash',
        GETDATE(),
        N'Paid at counter'
    );

    UPDATE TimeSlots SET Status = N'Booked' WHERE TimeSlotID = @Slot2;
END

-- Booking 3: User1, Tennis, Cancelled + Cancelled invoice
IF @Slot3 IS NOT NULL
BEGIN
    DECLARE @Booking3 INT;

    INSERT INTO Bookings (AccountID, TimeSlotID, CourtPriceAtOrder, ServiceTotalAtOrder, TotalAmount, Status, Notes, CancelReason)
    VALUES (2, @Slot3, @PriceTennis, 0, @PriceTennis, N'Cancelled', N'Sample cancelled', N'User cancelled');

    SET @Booking3 = SCOPE_IDENTITY();

    INSERT INTO Invoices (BookingID, InvoiceCode, SubTotal, DiscountAmount, FinalAmount, PaymentStatus, PaymentMethod, Note)
    VALUES (
        @Booking3,
        N'INV-' + RIGHT(N'000000' + CAST(@Booking3 AS NVARCHAR(10)), 6),
        @PriceTennis,
        0,
        @PriceTennis,
        N'Cancelled',
        NULL,
        N'Cancelled with booking'
    );

    -- Slot remains Available because booking cancelled (filtered unique allows another booking)
    -- Keep slot Available for re-book
    UPDATE TimeSlots SET Status = N'Available' WHERE TimeSlotID = @Slot3;
END
GO

-- -----------------------------------------------------------------------------
-- 8. Summary
-- -----------------------------------------------------------------------------
PRINT N'---------- SportZoneDB seed complete ----------';
SELECT N'Roles' AS [Table], COUNT(*) AS [Rows] FROM Roles
UNION ALL SELECT N'Accounts', COUNT(*) FROM Accounts
UNION ALL SELECT N'CourtTypes', COUNT(*) FROM CourtTypes
UNION ALL SELECT N'Courts', COUNT(*) FROM Courts
UNION ALL SELECT N'TimeSlots', COUNT(*) FROM TimeSlots
UNION ALL SELECT N'Services', COUNT(*) FROM Services
UNION ALL SELECT N'Bookings', COUNT(*) FROM Bookings
UNION ALL SELECT N'BookingServices', COUNT(*) FROM BookingServices
UNION ALL SELECT N'Invoices', COUNT(*) FROM Invoices;
GO

PRINT N'Demo login:';
PRINT N'  admin@sportzone.local / Admin@123  (Admin)';
PRINT N'  user1@sportzone.local / User@123   (User)';
PRINT N'  user2@sportzone.local / User@123   (User)';
GO
